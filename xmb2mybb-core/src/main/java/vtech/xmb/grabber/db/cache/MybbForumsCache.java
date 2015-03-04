package vtech.xmb.grabber.db.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.mybb.entities.MybbForum;
import vtech.xmb.grabber.db.mybb.repositories.MybbForumsRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbForum;
import vtech.xmb.grabber.db.xmb.entities.XmbPost;
import vtech.xmb.grabber.db.xmb.entities.XmbThread;

@Component
public class MybbForumsCache {

  @Autowired
  private MybbForumsRepository mybbForumsRepository;

  private volatile List<MybbForum> mybbForums = null;

  public synchronized List<MybbForum> findAll() {
    if (mybbForums == null) {
      System.out.println(String.format("Executing findAll"));

      mybbForums = (List<MybbForum>) mybbForumsRepository.findAll();
    }

    return mybbForums;
  }

  public synchronized void evictCache() {
    System.out.println(String.format("Evicting the MybbForumsCache"));
    mybbForums = null;
  }

  public MybbForum findByXmbForum(XmbForum xmbForum) {
    for (MybbForum mybbForum : findAll()) {
      if (mybbForum.xmbfid == null) {
        System.out.println(String.format("A Mybb forum with pid=%s and name=%s has a null value of xmbfid.", mybbForum.fid, mybbForum.name));

        continue;
      }

      if (mybbForum.xmbfid.equals(xmbForum.fid)) {
        return mybbForum;
      }
    }

    System.out.println(String.format("Can not find a Mybb forum for XMB forum with fid=%s, name=%s", xmbForum.fid, xmbForum.name));

    return null;
  }

  public MybbForum findByXmbPost(XmbPost xmbPost) {
    for (MybbForum mybbForum : findAll()) {
      if (mybbForum.xmbfid == null) {
        System.out.println(String.format("A Mybb forum with pid=%s and name=%s has a null value of xmbfid.", mybbForum.fid, mybbForum.name));

        continue;
      }

      if (mybbForum.xmbfid.equals(xmbPost.fid)) {
        return mybbForum;
      }
    }

    System.out.println(String.format("Can not find a forum for XMB post with pid=%s tid=%s, fid=%s, subject=%s", xmbPost.pid, xmbPost.tid, xmbPost.fid,
        xmbPost.subject));

    return null;
  }

  public MybbForum findByXmbThread(XmbThread xmbThread) {
    for (MybbForum mybbForum : findAll()) {
      if (mybbForum.xmbfid == null) {
        System.out.println(String.format("A Mybb forum with pid=%s and name=%s has a null value of xmbfid.", mybbForum.fid, mybbForum.name));

        continue;
      }

      if (mybbForum.xmbfid.equals(xmbThread.fid)) {
        return mybbForum;
      }
    }

    System.out.println(String.format("Can not find a forum for XMB thread with tid=%s, fid=%s, subject=%s", xmbThread.tid, xmbThread.fid, xmbThread.subject));

    return null;
  }
}
