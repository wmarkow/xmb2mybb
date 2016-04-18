package vtech.xmb.grabber.db.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.cache.MybbForumsCache;
import vtech.xmb.grabber.db.cache.XmbForumsCache;
import vtech.xmb.grabber.db.mybb.entities.MybbForum;
import vtech.xmb.grabber.db.mybb.repositories.MybbForumsRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbForum;

@Service
public class MigrateForums {
  private final static Logger LOGGER = Logger.getLogger(MigrateForums.class);
  private final static Logger ROOT_LOGGER = Logger.getRootLogger();

  @Autowired
  private XmbForumsCache xmbForumsCache;
  @Autowired
  private MybbForumsRepository mybbForumsRepository;
  @Autowired
  private MybbForumsCache mybbForumsCache;

  public void migrateForums() {
    LOGGER.info("Forums migration started.");
    ROOT_LOGGER.info("Forums migration started.");

    migrateForumsFirstStage();
    fixParents();
    fixParentsList();

    mybbForumsCache.evictCache();

    LOGGER.info(String.format("Found %s forums in MyBB after migration.", mybbForumsCache.getSize()));
    ROOT_LOGGER.info(String.format("Found %s forums in MyBB after migration.", mybbForumsCache.getSize()));
    LOGGER.info("Forums migration finished.");
    ROOT_LOGGER.info("Forums migration finished.");
  }

  private void migrateForumsFirstStage() {
    LOGGER.info(String.format("Found %s forums to migrate from XMB.", xmbForumsCache.findAll().size()));
    ROOT_LOGGER.info(String.format("Found %s forums to migrate from XMB.", xmbForumsCache.findAll().size()));

    for (XmbForum xmbForum : xmbForumsCache.findAll()) {
      MybbForum mybbForum = new MybbForum();

      mybbForum.setDefaults();

      mybbForum.active = 1;
      mybbForum.open = 1;
      mybbForum.description = xmbForum.description;
      mybbForum.disporder = xmbForum.displayorder;
      mybbForum.name = xmbForum.name;
      mybbForum.parentlist = "";
      mybbForum.password = xmbForum.password;
      mybbForum.pid = 0;
      mybbForum.xmbfid = xmbForum.fid;
      mybbForum.xmbpid = xmbForum.fup;

      if (xmbForum.type.equals("forum") || xmbForum.type.equals("sub")) {
        mybbForum.type = "f";
      } else {
        mybbForum.type = "c";
      }

      mybbForumsRepository.save(mybbForum);
    }
  }

  private void fixParents() {
    List<MybbForum> mybbForums = (List<MybbForum>) mybbForumsRepository.findAll();

    for (MybbForum mybbForum : mybbForums) {
      if (mybbForum.xmbfid == null || mybbForum.xmbpid == null) {
        continue;
      }

      final long parentId = findParentId(mybbForums, mybbForum);
      mybbForum.pid = parentId;
      mybbForumsRepository.save(mybbForum);
    }
  }

  private void fixParentsList() {
    List<MybbForum> mybbForums = (List<MybbForum>) mybbForumsRepository.findAll();

    for (MybbForum mybbForum : mybbForums) {
      if (mybbForum.xmbfid == null || mybbForum.xmbpid == null) {
        continue;
      }

      List<Long> parentList = new ArrayList<Long>();
      findParentList(parentList, mybbForums, mybbForum);

      Collections.reverse(parentList);
      StringBuilder sb = new StringBuilder();
      for (int q = 0; q < parentList.size(); q++) {
        sb.append(parentList.get(q));
        if (q < parentList.size() - 1) {
          sb.append(",");
        }
      }

      mybbForum.parentlist = sb.toString();
      mybbForumsRepository.save(mybbForum);
    }
  }

  public List<Long> findParentList(List<Long> parentList, List<MybbForum> mybbForums, MybbForum mybbForum) {
    if (mybbForum == null) {
      return parentList;
    }

    if (mybbForum.pid == 0) {
      parentList.add(mybbForum.fid);

      return parentList;
    }

    parentList.add(mybbForum.fid);

    return findParentList(parentList, mybbForums, findParent(mybbForums, mybbForum));
  }

  private long findParentId(List<MybbForum> mybbForums, MybbForum mybbForum) {
    if (mybbForum.xmbpid == 0) {
      return 0;
    }

    for (MybbForum _mybbForum : mybbForums) {
      if (_mybbForum.xmbfid == null || _mybbForum.xmbpid == null) {
        continue;
      }

      if (_mybbForum.xmbfid.equals(mybbForum.xmbpid)) {
        return _mybbForum.fid;
      }
    }

    throw new RuntimeException(String.format("Can not find the parent forum for Mybb Forum with id %s", mybbForum.fid));
  }

  private MybbForum findParent(List<MybbForum> mybbForums, MybbForum mybbForum) {
    if (mybbForum.pid == 0) {
      return null;
    }

    for (MybbForum _mybbForum : mybbForums) {
      if (_mybbForum.fid.equals(mybbForum.pid)) {
        return _mybbForum;
      }
    }

    return null;
  }
}
