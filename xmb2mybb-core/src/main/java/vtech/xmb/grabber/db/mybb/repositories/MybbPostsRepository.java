package vtech.xmb.grabber.db.mybb.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.mybb.entities.MybbPost;

@Repository
public interface MybbPostsRepository extends CrudRepository<MybbPost, Long> {
  public MybbPost findByXmbpid(Long xmbPid);
}
