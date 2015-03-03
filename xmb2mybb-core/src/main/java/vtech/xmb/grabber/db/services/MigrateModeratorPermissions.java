package vtech.xmb.grabber.db.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.mybb.entities.MybbForum;
import vtech.xmb.grabber.db.mybb.entities.MybbModerator;
import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.repositories.MybbForumsRepository;
import vtech.xmb.grabber.db.mybb.repositories.MybbModeratorsRepository;
import vtech.xmb.grabber.db.mybb.repositories.MybbUsersRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbForum;
import vtech.xmb.grabber.db.xmb.repositories.XmbForumsRepository;

@Service
public class MigrateModeratorPermissions {

  @Autowired
  private XmbForumsRepository xmbForumsRepository;
  @Autowired
  private MybbForumsRepository mybbForumsRepository;
  @Autowired
  private MybbUsersRepository mybbUsersRepository;
  @Autowired
  private MybbModeratorsRepository mybbModeratorsRepository;

  public void migrateModeratorPermissions() {
    List<XmbForum> xmbForums = (List<XmbForum>) xmbForumsRepository.findAll();
    List<MybbForum> mybbForums = (List<MybbForum>) mybbForumsRepository.findAll();
    List<MybbUser> mybbUsers = (List<MybbUser>) mybbUsersRepository.findAll();

    for (XmbForum xmbForum : xmbForums) {
      MybbForum mybbForum = findMybbForum(mybbForums, xmbForum.fid);

      for (String moderator : xmbForum.getModerators()) {
        MybbUser mybbUser = findUser(mybbUsers, moderator);

        if (mybbUser == null) {
          System.out.println(String.format("Could not find a user for username = \"%s\". Moderator rights will not be migrated.", moderator));

          continue;
        }

        MybbModerator mybbModerator = new MybbModerator();
        mybbModerator.fid = mybbForum.fid;
        mybbModerator.uid = mybbUser.uid;
        applyDefaults(mybbModerator);

        mybbModeratorsRepository.save(mybbModerator);
      }
    }
  }

  private MybbForum findMybbForum(List<MybbForum> mybbForums, long xmbForumId) {
    for (MybbForum mybbForum : mybbForums) {
      if (mybbForum.xmbfid == null) {
        continue;
      }

      if (mybbForum.xmbfid.equals(xmbForumId)) {
        return mybbForum;
      }
    }

    return null;
  }

  private MybbUser findUser(List<MybbUser> mybbUsers, String username) {
    for (MybbUser mybbUser : mybbUsers) {
      if (mybbUser.username.equals(username)) {
        return mybbUser;
      }
    }

    return null;
  }

  private void applyDefaults(MybbModerator mybbModerator) {
    mybbModerator.canapproveunapproveattachs = 1;
    mybbModerator.canapproveunapproveposts = 1;
    mybbModerator.canapproveunapprovethreads = 1;
    mybbModerator.candeleteposts = 1;
    mybbModerator.candeletethreads = 1;
    mybbModerator.caneditposts = 1;
    mybbModerator.canmanageannouncements = 1;
    mybbModerator.canmanagepolls = 1;
    mybbModerator.canmanagereportedposts = 1;
    mybbModerator.canmanagethreads = 1;
    mybbModerator.canmovetononmodforum = 1;
    mybbModerator.canopenclosethreads = 1;
    mybbModerator.canpostclosedthreads = 1;
    mybbModerator.canrestoreposts = 1;
    mybbModerator.canrestorethreads = 1;
    mybbModerator.cansoftdeleteposts = 1;
    mybbModerator.cansoftdeletethreads = 1;
    mybbModerator.canstickunstickthreads = 1;
    mybbModerator.canusecustomtools = 1;
    mybbModerator.canviewdeleted = 1;
    mybbModerator.canviewips = 1;
    mybbModerator.canviewmodlog = 1;
    mybbModerator.canviewunapprove = 1;
  }
}
