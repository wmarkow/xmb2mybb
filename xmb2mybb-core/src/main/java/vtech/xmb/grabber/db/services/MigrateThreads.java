package vtech.xmb.grabber.db.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.mybb.entities.MybbForum;
import vtech.xmb.grabber.db.mybb.entities.MybbThread;
import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.repositories.MybbForumsRepository;
import vtech.xmb.grabber.db.mybb.repositories.MybbThreadsRepository;
import vtech.xmb.grabber.db.mybb.repositories.MybbUsersRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbThread;
import vtech.xmb.grabber.db.xmb.repositories.XmbThreadsRepository;

@Service
public class MigrateThreads {

  @Autowired
  private XmbThreadsRepository xmbThreadsRepository;

  @Autowired
  private MybbThreadsRepository mybbThreadsRepository;
  @Autowired
  private MybbForumsRepository mybbForumsRepository;
  @Autowired
  private MybbUsersRepository mybbUsersRepository;

  public void migrateThreads() {
    migrateThreadsFirstStage();
  }

  private void migrateThreadsFirstStage() {
    final int pageSize = 1000;
    int pageNumber = 0;
    boolean shouldContinue = true;

    List<MybbForum> mybbForums = (List<MybbForum>) mybbForumsRepository.findAll();
    List<MybbUser> mybbUsers = (List<MybbUser>) mybbUsersRepository.findAll();

    Pageable pageRequest = new PageRequest(pageNumber, pageSize);

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
        final Long forumId = findForumId(mybbForums, xmbThread);
        if (forumId == null) {
          continue;
        }
        mybbThread.fid = forumId;
        mybbThread.sticky = xmbThread.topped;

        if (xmbThread.subject.length() > 120) {
          System.out.println(String.format("XMB thread tid=%s and subject=%s has too long subject (%s). It will be truncated to 120 characters", xmbThread.tid,
              xmbThread.subject, xmbThread.subject.length()));
          mybbThread.subject = xmbThread.subject.substring(0, 120);
        } else {
          mybbThread.subject = xmbThread.subject;
        }

        final MybbUser mybbUser = findUserByName(mybbUsers, xmbThread.author);
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

        mybbThreadsRepository.save(mybbThread);
      }
      pageRequest = pageRequest.next();
    }
  }

  private Long findForumId(List<MybbForum> mybbForums, XmbThread xmbThread) {
    for (MybbForum mybbForum : mybbForums) {
      if (mybbForum.xmbfid == null) {
        continue;
      }

      if (mybbForum.xmbfid.equals(xmbThread.fid)) {
        return mybbForum.fid;
      }
    }

    System.out.println(String.format("Can not find a forum for XMB thread with tid=%s, fid=%s, subject=%s", xmbThread.tid, xmbThread.fid, xmbThread.subject));

    return null;
  }

  private MybbUser findUserByName(List<MybbUser> mybbUsers, String username) {
    for (MybbUser mybbUser : mybbUsers) {
      if (mybbUser.username.equals(username)) {
        return mybbUser;
      }
    }

    return null;
  }
}
