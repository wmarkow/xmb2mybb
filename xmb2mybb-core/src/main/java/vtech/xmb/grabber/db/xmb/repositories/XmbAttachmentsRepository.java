package vtech.xmb.grabber.db.xmb.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.xmb.entities.XmbAttachment;

@Repository
public interface XmbAttachmentsRepository extends PagingAndSortingRepository<XmbAttachment, Long> {
}
