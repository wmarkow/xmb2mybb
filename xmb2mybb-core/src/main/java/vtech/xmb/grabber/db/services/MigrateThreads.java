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
import vtech.xmb.grabber.db.cache.MybbUsersCache;
import vtech.xmb.grabber.db.mybb.entities.MybbForum;
import vtech.xmb.grabber.db.mybb.entities.MybbThread;
import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.repositories.MybbThreadsRepository;
import vtech.xmb.grabber.db.services.fixers.FixersChain;
import vtech.xmb.grabber.db.services.fixers.HtmlEntityFixer;
import vtech.xmb.grabber.db.services.fixers.QuotesCharactersFixer;
import vtech.xmb.grabber.db.xmb.entities.XmbThread;
import vtech.xmb.grabber.db.xmb.repositories.XmbThreadsRepository;

@Service
public class MigrateThreads {
  private final static Logger LOGGER = Logger.getLogger(MigrateThreads.class);

  @Autowired
  private XmbThreadsRepository xmbThreadsRepository;
  @Autowired
  private MybbThreadsRepository mybbThreadsRepository;
  @Autowired
  private MybbForumsCache mybbForumsCache;
  @Autowired
  private MybbUsersCache mybbUsersCache;
  @Autowired
  private HtmlEntityFixer htmlEntityFixer;
  @Autowired
  private QuotesCharactersFixer quotesCharactersFixer;

  private FixersChain fixersChain;

  public void migrateThreads() {
    migrateThreadsFirstStage();
  }

  private void migrateThreadsFirstStage() {
    final int pageSize = 1000;
    int pageNumber = 0;
    boolean shouldContinue = true;

    Pageable pageRequest = new PageRequest(pageNumber, pageSize);

    FixersChain fixersChain = getFixersChain();

    while (shouldContinue) {
      Page<XmbThread> xmbThreadsPage = (Page<XmbThread>) xmbThreadsRepository.findAll(pageRequest);
      List<XmbThread> xmbThreads = xmbThreadsPage.getContent();

      if (xmbThreads.size() == 0) {
        shouldContinue = false;
        break;
      }

      for (XmbThread xmbThread : xmbThreads) {
        MybbThread mybbThread = new MybbThread();

        if ("yes".equals(xmbThread.closed)) {
          mybbThread.closed = 1;
        } else {
          mybbThread.closed = 0;
        }
        final MybbForum mybbForum = mybbForumsCache.findByXmbThread(xmbThread);
        if (mybbForum == null) {
          continue;
        }
        mybbThread.fid = mybbForum.fid;
        mybbThread.sticky = xmbThread.topped;
        mybbThread.subject = getFixedSubject(xmbThread);
        final MybbUser mybbUser = mybbUsersCache.findUserByName(xmbThread.author);
        if (mybbUser == null) {
          mybbThread.uid = 0L;
          mybbThread.username = xmbThread.author;
        } else {
          mybbThread.uid = mybbUser.uid;
          mybbThread.username = xmbThread.author;
        }

        mybbThread.xmbfid = xmbThread.fid;
        mybbThread.xmbtid = xmbThread.tid;
        mybbThread.visible = 1;
        mybbThread.notes = "";
        mybbThread.poll = 0;

        mybbThreadsRepository.save(mybbThread);
      }
      pageRequest = pageRequest.next();
    }
  }

  private FixersChain getFixersChain() {
    if (this.fixersChain == null) {
      fixersChain = new FixersChain();
      fixersChain.addFixerToChain(htmlEntityFixer);
      fixersChain.addFixerToChain(quotesCharactersFixer);
    }

    return fixersChain;
  }
  
  private String getFixedSubject(XmbThread xmbThread) {
    String fixedSubject = xmbThread.subject;
    try {
      fixedSubject = getFixersChain().fix(xmbThread.subject).getFixedText();
    } catch (ParseException e) {
      LOGGER.warn(e.getMessage(), e);
    }

    if (fixedSubject.length() > 120) {
      fixedSubject = fixedSubject.substring(0, 120);
      LOGGER.warn(String.format("XMB thread tid=%s and subject=%s has too long subject (%s). It will be truncated to 120 characters (%s)", xmbThread.tid,
          xmbThread.subject, xmbThread.subject.length(), fixedSubject));
    }
    
    return fixedSubject;
  }
}
