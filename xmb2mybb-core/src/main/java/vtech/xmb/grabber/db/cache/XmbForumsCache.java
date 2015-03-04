package vtech.xmb.grabber.db.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.xmb.entities.XmbForum;
import vtech.xmb.grabber.db.xmb.repositories.XmbForumsRepository;

@Component
public class XmbForumsCache {

  @Autowired
  private XmbForumsRepository xmbForumsRepository;

  private volatile List<XmbForum> xmbForum = null;

  public synchronized List<XmbForum> findAll() {
    if (xmbForum == null) {
      xmbForum = (List<XmbForum>) xmbForumsRepository.findAll();
    }

    return xmbForum;
  }

  public synchronized void evictCache() {
    System.out.println(String.format("Evicting the XmbForumsCache"));
    xmbForum = null;
  }
}
