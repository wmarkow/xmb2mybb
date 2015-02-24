package vtech.xmb.grabber.db.xmb.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.xmb.entities.XmbVoteResult;

@Repository
public interface XmbVoteResultRepository extends CrudRepository<XmbVoteResult, Long> {
  public List<XmbVoteResult> findByVoteId(Long voteId);
}
