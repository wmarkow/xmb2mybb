package vtech.xmb.grabber.db.mybb.repositories;

import org.springframework.data.repository.CrudRepository;

import vtech.xmb.grabber.db.mybb.entities.MybbPrivateMessage;

public interface MybbPrivateMessagesRepository extends CrudRepository<MybbPrivateMessage, Long> {

}
