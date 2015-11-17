package vtech.xmb.grabber.db.mybb.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.mybb.entities.MybbForum;
import vtech.xmb.grabber.db.mybb.entities.MybbThreadsSubscriptions;
import vtech.xmb.grabber.db.xmb.entities.XmbForum;

@Repository
public interface MybbThreadsSubscriptionsRepository extends CrudRepository<MybbThreadsSubscriptions, Long> {
}
