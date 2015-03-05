package vtech.xmb.grabber.db.cache;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.repositories.MybbUsersRepository;

@Component
public class MybbUsersCache {
  private final static Logger LOGGER = Logger.getLogger(MybbUsersCache.class);

  @Autowired
  private MybbUsersRepository mybbUsersRepository;

  private volatile List<MybbUser> mybbUsers = null;

  public synchronized List<MybbUser> findAll() {
    if (mybbUsers == null) {
      LOGGER.info(String.format("Executing MybbUsersRepository.findAll"));

      mybbUsers = (List<MybbUser>) mybbUsersRepository.findAll();
    }

    return mybbUsers;
  }

  public synchronized void evictCache() {
    LOGGER.info(String.format("Evicting the MyBB Users Cache "));
    mybbUsers = null;
  }

  public MybbUser findUserByName(String username) {
    for (MybbUser mybbUser : findAll()) {
      if (mybbUser.username.equals(username)) {
        return mybbUser;
      }
    }

    return null;
  }
}
