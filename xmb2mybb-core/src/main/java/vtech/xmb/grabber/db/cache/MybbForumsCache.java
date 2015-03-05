package vtech.xmb.grabber.db.cache;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.mybb.entities.MybbForum;
import vtech.xmb.grabber.db.mybb.repositories.MybbForumsRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbForum;
import vtech.xmb.grabber.db.xmb.entities.XmbPost;
import vtech.xmb.grabber.db.xmb.entities.XmbThread;

@Component
public class MybbForumsCache {
  private final static Logger LOGGER = Logger.getLogger(MybbForumsCache.class);

  @Autowired
  private MybbForumsRepository mybbForumsRepository;

  private volatile List<MybbForum> mybbForums = null;

  public synchronized List<MybbForum> findAll() {
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

  public MybbForum findByXmbForum(XmbForum xmbForum) {
    for (MybbForum mybbForum : findAll()) {
      if (mybbForum.xmbfid == null) {
        continue;
      }

      if (mybbForum.xmbfid.equals(xmbForum.fid)) {
        return mybbForum;
      }
    }

    return null;
  }

  public MybbForum findByXmbPost(XmbPost xmbPost) {
    for (MybbForum mybbForum : findAll()) {
      if (mybbForum.xmbfid == null) {
        continue;
      }

      if (mybbForum.xmbfid.equals(xmbPost.fid)) {
        return mybbForum;
      }
    }

    return null;
  }

  public MybbForum findByXmbThread(XmbThread xmbThread) {
    for (MybbForum mybbForum : findAll()) {
      if (mybbForum.xmbfid == null) {
        continue;
      }

      if (mybbForum.xmbfid.equals(xmbThread.fid)) {
        return mybbForum;
      }
    }

    return null;
  }
}