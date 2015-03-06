package vtech.xmb.grabber.db.mybb.repositories;

import org.springframework.data.repository.CrudRepository;

import vtech.xmb.grabber.db.mybb.entities.MybbAttachment;

public interface MybbAttachmentsRepository extends CrudRepository<MybbAttachment, Long> {
  public MybbAttachment findByXmbAid(long xmbAid);
}
