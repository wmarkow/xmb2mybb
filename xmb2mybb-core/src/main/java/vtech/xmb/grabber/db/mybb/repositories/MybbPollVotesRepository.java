package vtech.xmb.grabber.db.mybb.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import vtech.xmb.grabber.db.mybb.entities.MybbPoll;
import vtech.xmb.grabber.db.mybb.entities.MybbPollVote;

public interface MybbPollVotesRepository extends CrudRepository<MybbPollVote, Long> {
}
