package vtech.xmb.grabber.db.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  private volatile Map<Long, MybbThread> byXmbIdThreadsMap = null;

  public MybbThread findByXmbVoteDesc(XmbVoteDesc xmbVoteDesc) {
    return getByXmbIdThreadsMap().get(xmbVoteDesc.topicId);
  }

  public MybbThread findByXmbPost(XmbPost xmbPost) {
    return getByXmbIdThreadsMap().get(xmbPost.tid);
  }

  public synchronized void evictCache() {
    LOGGER.info(String.format("Evicting the MybbThreadsCache"));
    byXmbIdThreadsMap = null;
  }

  private synchronized Map<Long, MybbThread> getByXmbIdThreadsMap() {
    if (byXmbIdThreadsMap == null) {
      LOGGER.warn(String.format("MybbThreadsRepository.findAll()"));

      List<MybbThread> mybbThreads = (List<MybbThread>) mybbThreadsRepository.findAll();

      byXmbIdThreadsMap = new HashMap<Long, MybbThread>();
      for (MybbThread mybbThread : mybbThreads) {
        if (mybbThread.xmbtid == null) {
          continue;
        }
        if (byXmbIdThreadsMap.containsKey(mybbThread.xmbtid)) {
          final String message = String.format("MybbThreadsCache already contains a MyBB thread with xmbtid = %s", mybbThread.xmbtid);
          LOGGER.error(message);

          throw new RuntimeException(message);
        }

        byXmbIdThreadsMap.put(mybbThread.xmbtid, mybbThread);
      }
    }

    return byXmbIdThreadsMap;
  }
}
