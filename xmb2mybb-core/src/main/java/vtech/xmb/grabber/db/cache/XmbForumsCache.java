package vtech.xmb.grabber.db.cache;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.xmb.entities.XmbForum;
import vtech.xmb.grabber.db.xmb.repositories.XmbForumsRepository;

@Component
public class XmbForumsCache {
  private final static Logger LOGGER = Logger.getLogger(XmbForumsCache.class);
  
  @Autowired
  private XmbForumsRepository xmbForumsRepository;

  private volatile List<XmbForum> xmbForum = null;

  public synchronized List<XmbForum> findAll() {
    if (xmbForum == null) {
      LOGGER.info(String.format("Executing XmbForumsRepository.findAll"));
      
      xmbForum = (List<XmbForum>) xmbForumsRepository.findAll();
    }

    return xmbForum;
  }

  public synchronized void evictCache() {
    LOGGER.info(String.format("Evicting the XmbForumsCache"));
    xmbForum = null;
  }
}
