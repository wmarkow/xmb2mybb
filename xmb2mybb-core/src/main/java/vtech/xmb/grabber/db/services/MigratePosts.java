package vtech.xmb.grabber.db.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.cache.MybbForumsCache;
import vtech.xmb.grabber.db.cache.MybbThreadsCache;
import vtech.xmb.grabber.db.cache.MybbUsersCache;
import vtech.xmb.grabber.db.mybb.entities.MybbForum;
import vtech.xmb.grabber.db.mybb.entities.MybbPost;
import vtech.xmb.grabber.db.mybb.entities.MybbThread;
import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.repositories.MybbPostsRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbPost;
import vtech.xmb.grabber.db.xmb.repositories.XmbPostsRepository;

@Service
public class MigratePosts {
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

  public void migratePosts() {
    migratePostsFirstStage();
  }

  private void migratePostsFirstStage() {
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

        // derive forum id
        final MybbForum mybbForum = mybbForumsCache.findByXmbPost(xmbPost);
        if (mybbForum == null) {
          continue;
        }
        mybbPost.fid = mybbForum.fid;

        // derive thread id
        final MybbThread mybbThread = mybbThreadsCache.findByXmbPost(xmbPost);
        if (mybbThread == null) {
          continue;
        }
        mybbPost.tid = mybbThread.tid;

        if (xmbPost.subject.length() > 120) {
          System.out.println(String.format("XMB post pid=%s and tid=%s and subject=%s has too long subject (%s). It will be truncated to 120 characters",
              xmbPost.pid, xmbPost.tid, xmbPost.subject, xmbPost.subject.length()));
          mybbPost.subject = xmbPost.subject.substring(0, 120);
        } else {
          mybbPost.subject = xmbPost.subject;
        }

        mybbPost.dateline = xmbPost.dateline;
        mybbPost.message = xmbPost.message;

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
    }
  }
}
