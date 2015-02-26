package vtech.xmb.grabber.db.xmb.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.xmb.entities.XmbPost;

@Repository
public interface XmbPostsRepository extends PagingAndSortingRepository<XmbPost, Long> {
}
