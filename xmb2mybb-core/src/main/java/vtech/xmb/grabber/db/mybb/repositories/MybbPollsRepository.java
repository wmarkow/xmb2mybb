package vtech.xmb.grabber.db.mybb.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import vtech.xmb.grabber.db.mybb.entities.MybbPoll;

public interface MybbPollsRepository extends CrudRepository<MybbPoll, Long> {
  public MybbPoll findByQuestion(@Param("question") String question);
}
