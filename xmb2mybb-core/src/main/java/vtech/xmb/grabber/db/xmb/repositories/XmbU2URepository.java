package vtech.xmb.grabber.db.xmb.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.xmb.entities.XmbU2U;

@Repository
public interface XmbU2URepository extends PagingAndSortingRepository<XmbU2U, Long> {
}
