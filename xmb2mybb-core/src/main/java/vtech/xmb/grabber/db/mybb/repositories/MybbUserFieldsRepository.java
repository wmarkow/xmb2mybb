package vtech.xmb.grabber.db.mybb.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.entities.MybbUserFields;

@Repository
public interface MybbUserFieldsRepository extends CrudRepository<MybbUserFields, Long> {
}
