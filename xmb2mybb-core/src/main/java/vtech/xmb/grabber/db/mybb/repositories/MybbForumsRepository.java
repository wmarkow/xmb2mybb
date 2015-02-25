package vtech.xmb.grabber.db.mybb.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.mybb.entities.MybbForum;
import vtech.xmb.grabber.db.xmb.entities.XmbForum;

@Repository
public interface MybbForumsRepository extends CrudRepository<MybbForum, Long> {
}
