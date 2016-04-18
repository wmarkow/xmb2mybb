package vtech.xmb.grabber.db.services.fixers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.domain.fixers.FixResult;
import vtech.xmb.grabber.db.mybb.entities.MybbAttachment;
import vtech.xmb.grabber.db.mybb.repositories.MybbAttachmentsRepository;

@Component
public class FileFixer {
  private final static Logger LOGGER = Logger.getLogger(FileFixer.class);

  @Autowired
  private MybbAttachmentsRepository mybbAttachmentsRepository;

  public FixResult fix(final String textToFix, long mybbPid, long xmbPid) {
    FixResult fixResult = new FixResult();
    String result = textToFix;

    Pattern pattern = Pattern.compile("\\[file.*?\\].*?\\[/file\\]");
    Matcher matcher = pattern.matcher(result);

    Map<String, String> files = new HashMap<String, String>();

    while (matcher.find()) {
      final String fileAsString = matcher.group();
      final String attachmentAsString = replaceFileWithAttachment(fileAsString, mybbPid, xmbPid);

      files.put(fileAsString, attachmentAsString);
    }

    for (String file : files.keySet()) {
      result = result.replace(file, files.get(file));
      fixResult.setFixRequired(true);
    }

    fixResult.setFixedText(result);
    
    return fixResult;
  }

  private String replaceFileWithAttachment(String fileAsString, long mybbPid, long xmbPid) {
    final long xmbAttachmentId = Long.valueOf(fileAsString.replace("[file]", "").replace("[/file]", "").trim());

    MybbAttachment mybbAttachment = mybbAttachmentsRepository.findByXmbAid(xmbAttachmentId);
    if (mybbAttachment == null) {
      LOGGER.warn(String.format(
          "MyBB post with pid=%s and xmbpid=%s contains reference to non existing XMB file/attachment (%s). Will replace it with '[attachment=0]'", mybbPid,
          xmbPid, fileAsString));

      return "[attachment=0]";
    }

    return String.format("[attachment=%s]", mybbAttachment.aid);
  }
}
