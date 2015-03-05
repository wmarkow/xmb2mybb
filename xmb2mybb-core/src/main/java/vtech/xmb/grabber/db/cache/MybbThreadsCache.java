package vtech.xmb.grabber.db.cache;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.mybb.entities.MybbThread;
import vtech.xmb.grabber.db.mybb.repositories.MybbThreadsRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbPost;
import vtech.xmb.grabber.db.xmb.entities.XmbVoteDesc;

@Component
public class MybbThreadsCache {
  private final static Logger LOGGER = Logger.getLogger(MybbThreadsCache.class);

  @Autowired
  private MybbThreadsRepository mybbThreadsRepository;

  private volatile List<MybbThread> mybbThreads = null;

  public synchronized List<MybbThread> findAll() {
    if (mybbThreads == null) {
      LOGGER.warn(String.format("MybbThreadsRepository.findAll()"));

      mybbThreads = (List<MybbThread>) mybbThreadsRepository.findAll();
    }

    return mybbThreads;
  }

  public synchronized void evictCache() {
    LOGGER.info(String.format("Evicting the MybbThreadsCache"));
    mybbThreads = null;
  }

  public MybbThread findByXmbVoteDesc(XmbVoteDesc xmbVoteDesc) {
    for (MybbThread mybbThread : findAll()) {
      if (mybbThread.xmbtid == null) {
        continue;
      }

      if (mybbThread.xmbtid.equals(xmbVoteDesc.topicId)) {
        return mybbThread;
      }
    }

    return null;
  }

  public MybbThread findByXmbPost(XmbPost xmbPost) {
    for (MybbThread mybbThread : findAll()) {
      if (mybbThread.xmbtid == null) {
        continue;
      }

      if (mybbThread.xmbtid.equals(xmbPost.tid)) {
        return mybbThread;
      }
    }

    return null;
  }
}
