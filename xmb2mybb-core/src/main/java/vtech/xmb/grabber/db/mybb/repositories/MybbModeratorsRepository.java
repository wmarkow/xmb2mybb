package vtech.xmb.grabber.db.mybb.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.mybb.entities.MybbModerator;

@Repository
public interface MybbModeratorsRepository extends CrudRepository<MybbModerator, Long> {
}
