package vtech.xmb.grabber.db.cache;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.mybb.entities.MybbForum;
import vtech.xmb.grabber.db.mybb.repositories.MybbForumsRepository;

@Component
public class MybbForumsCache {
  private final static Logger LOGGER = Logger.getLogger(MybbForumsCache.class);

  @Autowired
  private MybbForumsRepository mybbForumsRepository;

  private volatile List<MybbForum> mybbForums = null;

  private synchronized List<MybbForum> findAll() {
    if (mybbForums == null) {
      LOGGER.info("Executing MybbForumsRepository.findAll()");

      mybbForums = (List<MybbForum>) mybbForumsRepository.findAll();
    }

    return mybbForums;
  }

  public synchronized void evictCache() {
    LOGGER.info("Evicting the MybbForumsCache");

    mybbForums = null;
  }

  public int getSize() {
    return findAll().size();
  }

  public MybbForum findByXmbForumId(Long xmbForumId) {
    for (MybbForum mybbForum : findAll()) {
      if (mybbForum.xmbfid == null) {
        continue;
      }

      if (mybbForum.xmbfid.equals(xmbForumId)) {
        return mybbForum;
      }
    }

    return null;
  }
}
