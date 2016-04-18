package vtech.xmb.grabber.db.mybb.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.mybb.entities.MybbPost;

@Repository
public interface MybbPostsRepository extends PagingAndSortingRepository<MybbPost, Long> {
  public MybbPost findByXmbpid(Long xmbPid);
}
