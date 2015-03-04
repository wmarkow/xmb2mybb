package vtech.xmb.grabber.db.mybb.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.repositories.MybbUsersRepository;

@Repository
public class MybbUsersCache {

  @Autowired
  private MybbUsersRepository mybbUsersRepository;

  private volatile List<MybbUser> mybbUsers = null;

  public synchronized List<MybbUser> findAll() {
    if (mybbUsers == null) {
      System.out.println(String.format("Executing findAll"));

      mybbUsers = (List<MybbUser>) mybbUsersRepository.findAll();
    }

    return mybbUsers;
  }

  public synchronized void evictCache() {
    System.out.println(String.format("Evicting the MyBB Users Cache "));
    mybbUsers = null;
  }

  public MybbUser findUserByName(String username) {
    for (MybbUser mybbUser : findAll()) {
      if (mybbUser.username.equals(username)) {
        return mybbUser;
      }
    }

    System.out.println(String.format("Could not find a MybbUser with name %s", username));

    return null;
  }
}
