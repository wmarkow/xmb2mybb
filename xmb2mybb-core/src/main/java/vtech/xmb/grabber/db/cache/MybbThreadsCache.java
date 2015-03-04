package vtech.xmb.grabber.db.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.mybb.entities.MybbThread;
import vtech.xmb.grabber.db.mybb.repositories.MybbThreadsRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbPost;
import vtech.xmb.grabber.db.xmb.entities.XmbVoteDesc;

@Component
public class MybbThreadsCache {

  @Autowired
  private MybbThreadsRepository mybbThreadsRepository;

  private volatile List<MybbThread> mybbThreads = null;

  public synchronized List<MybbThread> findAll() {
    if (mybbThreads == null) {
      System.out.println(String.format("Executing findAll"));

      mybbThreads = (List<MybbThread>) mybbThreadsRepository.findAll();
    }

    return mybbThreads;
  }

  public synchronized void evictCache() {
    System.out.println(String.format("Evicting the MybbForumsCache"));
    mybbThreads = null;
  }

  public MybbThread findByXmbVoteDesc(XmbVoteDesc xmbVoteDesc) {
    for (MybbThread mybbThread : mybbThreads) {
      if (mybbThread.xmbtid == null) {
        System.out.println(String.format("A Mybb thread with tid=%s fid=%s and subject=%s has a null value of xmbtid.", mybbThread.tid, mybbThread.fid,
            mybbThread.subject));

        continue;
      }

      if (mybbThread.xmbtid.equals(xmbVoteDesc.topicId)) {
        return mybbThread;
      }
    }

    System.out.println(String.format("Can not find a thread for XMB Vote Desc with voteId=%s tid=%s, voteText=%s", xmbVoteDesc.voteId, xmbVoteDesc.topicId,
        xmbVoteDesc.voteText));

    return null;
  }

  public MybbThread findByXmbPost(XmbPost xmbPost) {
    for (MybbThread mybbThread : mybbThreads) {
      if (mybbThread.xmbtid == null) {
        System.out.println(String.format("A Mybb thread with tid=%s fid=%s and subject=%s has a null value of xmbtid.", mybbThread.tid, mybbThread.fid,
            mybbThread.subject));

        continue;
      }

      if (mybbThread.xmbtid.equals(xmbPost.tid)) {
        return mybbThread;
      }
    }

    System.out.println(String.format("Can not find a thread for XMB post with pid=%s tid=%s, fid=%s, subject=%s", xmbPost.pid, xmbPost.tid, xmbPost.fid,
        xmbPost.subject));

    return null;
  }
}
