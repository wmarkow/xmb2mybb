package vtech.xmb.grabber.db.xmb.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.xmb.entities.XmbThread;

@Repository
public interface XmbThreadsRepository extends PagingAndSortingRepository<XmbThread, Long> {
}
