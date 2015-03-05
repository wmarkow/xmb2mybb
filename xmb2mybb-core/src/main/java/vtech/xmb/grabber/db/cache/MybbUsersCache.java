package vtech.xmb.grabber.db.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  private volatile Map<String, MybbUser> byUsernameUsersMap = null;

  public synchronized void evictCache() {
    LOGGER.info(String.format("Evicting the MyBB Users Cache "));
    byUsernameUsersMap = null;
  }

  public MybbUser findUserByName(String username) {
    return getByUsernameUsersMap().get(username);
  }
  
  private synchronized Map<String, MybbUser> getByUsernameUsersMap() {
    if (byUsernameUsersMap == null) {
      LOGGER.info(String.format("Executing MybbUsersRepository.findAll"));

      List<MybbUser> mybbUsers = (List<MybbUser>) mybbUsersRepository.findAll();

      byUsernameUsersMap = new HashMap<String, MybbUser>();
      for (MybbUser mybbUser : mybbUsers) {

        if (byUsernameUsersMap.containsKey(mybbUser.username)) {
          final String message = String.format("MybbUsersCache already contains a MyBB user with name = %s", mybbUser.username);
          LOGGER.error(message);

          throw new RuntimeException(message);
        }

        byUsernameUsersMap.put(mybbUser.username, mybbUser);
      }
    }

    return byUsernameUsersMap;
  }
}
