package vtech.xmb.grabber.db.services;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

import vtech.xmb.grabber.db.domain.ProgressCalculator;
import vtech.xmb.grabber.db.mybb.entities.MybbAttachment;
import vtech.xmb.grabber.db.mybb.entities.MybbPost;
import vtech.xmb.grabber.db.mybb.repositories.MybbAttachmentsRepository;
import vtech.xmb.grabber.db.mybb.repositories.MybbPostsRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbAttachment;
import vtech.xmb.grabber.db.xmb.repositories.XmbAttachmentsRepository;

@Service
public class MigrateAttachments {
  private final static Logger LOGGER = Logger.getLogger(MigrateAttachments.class);
  private final static Logger ROOT_LOGGER = Logger.getRootLogger();

  @Autowired
  private XmbAttachmentsRepository xmbAttachmentsRepository;
  @Autowired
  private MybbPostsRepository mybbPostsRepository;
  @Autowired
  private MybbAttachmentsRepository mybbAttachmentsRepository;

  @Value("${xmb.attachments.path}")
  private String xmbAttachmentsPath;
  @Value("${mybb.attachments.path}")
  private String mybbAttachmentsPath;

  public void migrateAttachments() {
    LOGGER.info("Posts attachments started.");
    ROOT_LOGGER.info("Posts attachments started.");

    final long xmbCount = xmbAttachmentsRepository.count();
    ProgressCalculator progressCalc = new ProgressCalculator(xmbCount);
    LOGGER.info(String.format("Found %s attachments to migrate from XMB.", xmbCount));
    ROOT_LOGGER.info(String.format("Found %s attachments to migrate from XMB.", xmbCount));

    final int pageSize = 1000;
    int pageNumber = 0;
    boolean shouldContinue = true;

    int thumbnailsCount = 0;
    
    Pageable pageRequest = new PageRequest(pageNumber, pageSize);

    while (shouldContinue) {
      Page<XmbAttachment> xmbAttachmentsPage = (Page<XmbAttachment>) xmbAttachmentsRepository.findAll(pageRequest);
      List<XmbAttachment> xmbAttachments = xmbAttachmentsPage.getContent();

      if (xmbAttachments.size() == 0) {
        shouldContinue = false;
        break;
      }

      for (XmbAttachment xmbAttachment : xmbAttachments) {
        if (xmbAttachment.parentid > 0) {
          // this is an image thumbnail; do not migrate it
          thumbnailsCount ++;
          continue;
        }

        MybbAttachment mybbAttachment = new MybbAttachment();

        MybbPost mybbPost = mybbPostsRepository.findByXmbpid(xmbAttachment.pid);
        if (mybbPost == null) {
          LOGGER.warn(String.format(
              "MyBB Post has not been found for XMB Attachment with aid=%s, pid=%s filename=%s. This XMB Atachment will not be migrated.", xmbAttachment.aid,
              xmbAttachment.pid, xmbAttachment.filename));

          continue;
        }

        // file path
        final String derivedAttachFileName = deriveAttachFileName(mybbPost.dateline, mybbPost.uid, xmbAttachment.filename);
        mybbAttachment.attachname = derivedAttachFileName;
        mybbAttachment.dateuploaded = mybbPost.dateline;
        mybbAttachment.downloads = xmbAttachment.downloads;
        mybbAttachment.filename = xmbAttachment.filename;
        mybbAttachment.filesize = xmbAttachment.filesize;
        mybbAttachment.filetype = xmbAttachment.filetype;
        mybbAttachment.pid = mybbPost.pid;
        mybbAttachment.uid = mybbPost.uid;
        mybbAttachment.visible = 1;
        mybbAttachment.xmbAid = xmbAttachment.aid;

        // copy file content
        try {
          if (xmbAttachment.attachment != null && xmbAttachment.attachment.length > 0) {
            copyByteArrayToFile(xmbAttachment.attachment, derivedAttachFileName);
          } else {
            File xmbAttachmentFile = createXmbAttachmentFile(xmbAttachment.subdir, xmbAttachment.aid);
            if (!xmbAttachmentFile.exists()) {
              LOGGER.warn(String.format(
                  "XMB attachment file %s does not exist for XMB attachment with aid=%s and filename=%s. This attachment will not be migrated.",
                  xmbAttachmentFile.getAbsolutePath(), xmbAttachment.aid, xmbAttachment.filename));

              continue;
            }
            copyFileToFile(xmbAttachment.subdir, xmbAttachment.aid, derivedAttachFileName);
          }

          mybbAttachmentsRepository.save(mybbAttachment);
        } catch (IOException e) {
          LOGGER.warn(String.format("Error while copying attachment file contents. XMB attachment with aid=%s filename=%s will not me migrated.",
              xmbAttachment.aid, xmbAttachment.filename));
          LOGGER.error(e.getMessage(), e);
        }
      }

      pageRequest = pageRequest.next();

      progressCalc.hit(xmbAttachments.size());
      progressCalc.logProgress(LOGGER, ROOT_LOGGER);
    }

    final long mybbCount = mybbAttachmentsRepository.count();
    final long notMigrated = xmbCount - mybbCount;
    LOGGER.info(String.format("Found %s attachments in MyBB after migration. %s attachments not migrated (%s are thumbnails).", mybbCount, notMigrated, thumbnailsCount));
    ROOT_LOGGER.info(String.format("Found %s attachments in MyBB after migration. %s attachments not migrated (%s are thumbnails).", mybbCount, notMigrated, thumbnailsCount));
    LOGGER.info("Attachments migration finished.");
    ROOT_LOGGER.info("Attachments migration finished.");
  }

  private String deriveAttachFileName(long dateline, long userId, String attachmentname) {
    // "201502/post_18_1423508246_cc0733c00785135f22daa349325a0ac4.attach"
    StringBuilder sb = new StringBuilder();
    sb.append(deriveFolderName(dateline));
    sb.append(File.separator);
    sb.append("post_");
    sb.append(userId);
    sb.append("_");
    sb.append(dateline);
    sb.append("_");
    try {
      sb.append(DigestUtils.md5DigestAsHex(attachmentname.getBytes("UTF-8")));
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
    sb.append(".attach");

    return sb.toString();
  }

  private String deriveFolderName(long dateline) {
    DateTime dateTime = new DateTime(1000 * dateline);
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMM");

    return dateTime.toString(formatter);
  }

  private void copyByteArrayToFile(byte[] bytes, String derivedAttachFileName) throws IOException {
    File base = new File(mybbAttachmentsPath);
    File result = new File(base, derivedAttachFileName);

    result.getParentFile().mkdirs();

    FileCopyUtils.copy(bytes, result);
  }

  private void copyFileToFile(String xmbSubDir, long xmbAid, String derivedAttachFileName) throws IOException {
    File mybbAttachmentFile = createMybbAttachmentFile(derivedAttachFileName);
    File xmbAttachmentFile = createXmbAttachmentFile(xmbSubDir, xmbAid);

    mybbAttachmentFile.getParentFile().mkdirs();

    FileCopyUtils.copy(xmbAttachmentFile, mybbAttachmentFile);

  }

  private File createXmbAttachmentFile(String xmbSubDir, long xmbAid) {
    File baseSource = new File(xmbAttachmentsPath);
    File resultSource = new File(baseSource, xmbSubDir);
    File resultSource2 = new File(resultSource, String.valueOf(xmbAid));

    return resultSource2;
  }

  private File createMybbAttachmentFile(String derivedAttachFileName) {
    File baseDestination = new File(mybbAttachmentsPath);
    File resultDestination = new File(baseDestination, derivedAttachFileName);

    return resultDestination;
  }
}
