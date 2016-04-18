package vtech.xmb.grabber.db.xmb.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.xmb.entities.XmbForum;
import vtech.xmb.grabber.db.xmb.entities.XmbSmiley;

@Repository
public interface XmbSmiliesRepository extends CrudRepository<XmbSmiley, Long> {
}
