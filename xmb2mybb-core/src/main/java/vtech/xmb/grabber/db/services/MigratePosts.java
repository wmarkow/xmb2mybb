package vtech.xmb.grabber.db.services;

import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.cache.MybbForumsCache;
import vtech.xmb.grabber.db.cache.MybbThreadsCache;
import vtech.xmb.grabber.db.cache.MybbUsersCache;
import vtech.xmb.grabber.db.domain.ProgressCalculator;
import vtech.xmb.grabber.db.mybb.entities.MybbPost;
import vtech.xmb.grabber.db.mybb.entities.MybbThread;
import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.repositories.MybbPostsRepository;
import vtech.xmb.grabber.db.services.fixers.FixersChain;
import vtech.xmb.grabber.db.services.fixers.HtmlEntityFixer;
import vtech.xmb.grabber.db.services.fixers.QuotesCharactersFixer;
import vtech.xmb.grabber.db.xmb.entities.XmbPost;
import vtech.xmb.grabber.db.xmb.repositories.XmbPostsRepository;

@Service
public class MigratePosts {
  private final static Logger LOGGER = Logger.getLogger(MigratePosts.class);
  private final static Logger ROOT_LOGGER = Logger.getRootLogger();

  @Autowired
  private XmbPostsRepository xmbPostsRepository;
  @Autowired
  private MybbThreadsCache mybbThreadsCache;
  @Autowired
  private MybbPostsRepository mybbPostsRepository;
  @Autowired
  private MybbForumsCache mybbForumsCache;
  @Autowired
  private MybbUsersCache mybbUsersCache;
  @Autowired
  private HtmlEntityFixer htmlEntityFixer;
  @Autowired
  private QuotesCharactersFixer quotesCharactersFixer;

  private FixersChain fixersChain;

  public void migratePosts() {
    migratePostsFirstStage();
  }

  private void migratePostsFirstStage() {
    LOGGER.info("Posts migration started.");
    ROOT_LOGGER.info("Posts migration started.");

    final long xmbCount = xmbPostsRepository.count();
    ProgressCalculator progressCalc = new ProgressCalculator(xmbCount);
    LOGGER.info(String.format("Found %s posts to migrate from XMB.", xmbCount));
    ROOT_LOGGER.info(String.format("Found %s posts to migrate from XMB.", xmbCount));

    final int pageSize = 1000;
    int pageNumber = 0;
    boolean shouldContinue = true;

    Pageable pageRequest = new PageRequest(pageNumber, pageSize);

    while (shouldContinue) {
      Page<XmbPost> xmbPostsPage = (Page<XmbPost>) xmbPostsRepository.findAll(pageRequest);
      List<XmbPost> xmbPosts = xmbPostsPage.getContent();

      if (xmbPosts.size() == 0) {
        shouldContinue = false;
        break;
      }

      for (XmbPost xmbPost : xmbPosts) {
        MybbPost mybbPost = new MybbPost();

        // derive thread id
        final MybbThread mybbThread = mybbThreadsCache.findByXmbPost(xmbPost);
        if (mybbThread == null) {
          LOGGER.warn(String.format("MyBB thread with XMB tid=%s does not exist. XMB post with pid=%s, tid=%s, fid=%s and subject=%s will not be migrated.",
              xmbPost.tid, xmbPost.pid, xmbPost.tid, xmbPost.fid, xmbPost.subject));
          continue;
        }

        mybbPost.fid = mybbThread.fid;
        mybbPost.tid = mybbThread.tid;
        mybbPost.subject = getFixedSubject(xmbPost);
        mybbPost.message = getFixedMessage(xmbPost);
        mybbPost.dateline = xmbPost.dateline;

        final MybbUser mybbUser = mybbUsersCache.findUserByName(xmbPost.author);
        if (mybbUser == null) {
          mybbPost.uid = 0L;
          mybbPost.username = xmbPost.author;
        } else {
          mybbPost.uid = mybbUser.uid;
          mybbPost.username = xmbPost.author;
        }

        mybbPost.xmbpid = xmbPost.pid;
        mybbPost.xmbtid = xmbPost.tid;
        mybbPost.xmbfid = xmbPost.fid;

        mybbPost.visible = 1;

        mybbPostsRepository.save(mybbPost);
      }
      pageRequest = pageRequest.next();

      progressCalc.hit(xmbPosts.size());
      progressCalc.logProgress(1, LOGGER, ROOT_LOGGER);
    }

    final long mybbCount = mybbPostsRepository.count();
    final long notMigrated = xmbCount - mybbCount;
    LOGGER.info(String.format("Found %s posts in MyBB after migration. %s posts not migrated.", mybbCount, notMigrated));
    ROOT_LOGGER.info(String.format("Found %s posts in MyBB after migration. %s posts not migrated.", mybbCount, notMigrated));
    LOGGER.info("Posts migration finished.");
    ROOT_LOGGER.info("Posts migration finished.");
  }

  private FixersChain getFixersChain() {
    if (this.fixersChain == null) {
      fixersChain = new FixersChain();
      fixersChain.addFixerToChain(htmlEntityFixer);
      fixersChain.addFixerToChain(quotesCharactersFixer);
    }

    return fixersChain;
  }

  private String getFixedSubject(XmbPost xmbPost) {
    String fixedSubject = xmbPost.subject;
    try {
      fixedSubject = getFixersChain().fix(xmbPost.subject).getFixedText();
    } catch (ParseException e) {
      LOGGER.warn(e.getMessage(), e);
    }

    if (fixedSubject.length() > 120) {
      fixedSubject = fixedSubject.substring(0, 120);
      LOGGER.warn(String.format("XMB post pid=%s and tid=%s and subject=%s has too long subject (%s characters). It will be truncated to 120 characters (%s)",
          xmbPost.pid, xmbPost.tid, xmbPost.subject, xmbPost.subject.length(), fixedSubject));
    }

    return fixedSubject;
  }

  private String getFixedMessage(XmbPost xmbPost) {
    try {
      return getFixersChain().fix(xmbPost.message).getFixedText();
    } catch (ParseException e) {
      LOGGER.warn(String.format("Parse exception while fixing the XMB post with pid=%s. Post will not be fixed.", xmbPost.pid), e);
    }

    return xmbPost.message;
  }
}
