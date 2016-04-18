package vtech.xmb.grabber.db.xmb.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.xmb.entities.XmbVoteDesc;

@Repository
public interface XmbVoteDescRepository extends CrudRepository<XmbVoteDesc, Long> {
}
