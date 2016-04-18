package vtech.xmb.grabber.db.services;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.cache.MybbThreadsCache;
import vtech.xmb.grabber.db.cache.MybbUsersCache;
import vtech.xmb.grabber.db.mybb.entities.MybbThread;
import vtech.xmb.grabber.db.mybb.entities.MybbThreadsSubscriptions;
import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.repositories.MybbThreadsSubscriptionsRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbFavorites;
import vtech.xmb.grabber.db.xmb.repositories.XmbFavoritesRepository;

@Service
public class MigrateForumSubscriptions {
  private final static Logger LOGGER = Logger.getLogger(MigrateForumSubscriptions.class);
  private final static Logger ROOT_LOGGER = Logger.getRootLogger();

  @Autowired
  private MybbThreadsCache mybbThreadsCache;
  @Autowired
  private MybbUsersCache mybbUsersCache;
  @Autowired
  private MybbThreadsSubscriptionsRepository mybbThreadsSubscriptionsRepository;
  @Autowired
  private XmbFavoritesRepository xmbFavoritesRepository;

  private MessageDigest md5;

  public void migrate() {
    LOGGER.info("Thread subscriptions migration started.");
    ROOT_LOGGER.info("Thread subscriptions migration started.");

    long dateline = new DateTime().getMillis() / 1000;

    for (XmbFavorites xmbFavorite : xmbFavoritesRepository.findAll()) {
      if (!"subscription".equals(xmbFavorite.type)) {
        continue;
      }

      MybbThread mybbThread = mybbThreadsCache.findByXmbThreadId(xmbFavorite.tid);
      if (mybbThread == null) {
        LOGGER.warn(String.format("There is no MyBB thread for XMB thread id %s. Thread subscription of that thread will not be migrated.", xmbFavorite.tid));
        continue;
      }

      MybbUser mybbUser = mybbUsersCache.findUserByName(xmbFavorite.username);
      if (mybbUser == null) {
        LOGGER.warn(String.format("There is no MyBB user with name '%s'. Thread subscription of that thread will not be migrated.", xmbFavorite.username));
        continue;
      }

      MybbThreadsSubscriptions mybbThreadSubscription = new MybbThreadsSubscriptions();
      mybbThreadSubscription.tid = mybbThread.tid;
      mybbThreadSubscription.uid = mybbUser.uid;
      mybbThreadSubscription.notification = 2; // notification by email
      mybbThreadSubscription.dateline = dateline;
      mybbThreadSubscription.subscriptionkey = deriveSybscriptionKey(dateline, mybbUser.uid, mybbThread.tid);// md5(TIME_NOW.$uid.$tid);

      mybbThreadsSubscriptionsRepository.save(mybbThreadSubscription);
    }

    LOGGER.info("Thread subscriptions migration finished.");
    ROOT_LOGGER.info("Thread subscriptions migration finished.");
  }

  private String deriveSybscriptionKey(long dateline, long uid, long tid) {
    StringBuilder sb = new StringBuilder();
    sb.append(dateline);
    sb.append(uid);
    sb.append(tid);

    try {
      return DigestUtils.md5Hex("asd".getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @PostConstruct
  private void init() throws NoSuchAlgorithmException {
    md5 = MessageDigest.getInstance("MD5");
  }
}
