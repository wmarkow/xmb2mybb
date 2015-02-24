package vtech.xmb.grabber.db.mybb.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.mybb.entities.MybbUser;

@Repository
public interface MybbUsersRepository extends CrudRepository<MybbUser, Long> {
}
