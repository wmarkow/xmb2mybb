package vtech.xmb.grabber.db.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.cache.MybbForumsCache;
import vtech.xmb.grabber.db.cache.MybbUsersCache;
import vtech.xmb.grabber.db.cache.XmbForumsCache;
import vtech.xmb.grabber.db.mybb.entities.MybbForum;
import vtech.xmb.grabber.db.mybb.entities.MybbModerator;
import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.repositories.MybbModeratorsRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbForum;

@Service
public class MigrateModeratorPermissions {
  private final static Logger LOGGER = Logger.getLogger(MigrateModeratorPermissions.class);
  private final static Logger ROOT_LOGGER = Logger.getRootLogger();

  @Autowired
  private XmbForumsCache xmbForumsCache;
  @Autowired
  private MybbForumsCache mybbForumsCache;
  @Autowired
  private MybbUsersCache mybbUsersCache;
  @Autowired
  private MybbModeratorsRepository mybbModeratorsRepository;

  public void migrateModeratorPermissions() {
    LOGGER.info("Moderators migration started.");
    ROOT_LOGGER.info("Moderators migration started.");

    for (XmbForum xmbForum : xmbForumsCache.findAll()) {
      MybbForum mybbForum = mybbForumsCache.findByXmbForumId(xmbForum.fid);

      for (String moderator : xmbForum.getModerators()) {
        MybbUser mybbUser = mybbUsersCache.findUserByName(moderator);

        if (mybbUser == null) {
          LOGGER.warn(String.format("Could not find a user with username = '%s' (a moderator of forum '%s'). Moderator rights will not be migrated.", moderator,
              xmbForum.name));

          continue;
        }

        MybbModerator mybbModerator = new MybbModerator();
        mybbModerator.fid = mybbForum.fid;
        mybbModerator.uid = mybbUser.uid;
        applyDefaults(mybbModerator);

        mybbModeratorsRepository.save(mybbModerator);
      }
    }

    LOGGER.info("Moderators migration finished.");
    ROOT_LOGGER.info("Moderators migration finished.");
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
