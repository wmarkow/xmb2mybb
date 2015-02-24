package vtech.xmb.grabber.db.xmb.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.xmb.entities.XmbU2U;

@Repository
public interface XmbU2URepository extends CrudRepository<XmbU2U, Long> {
	public List<XmbU2U> findAllByType(@Param("type") String type);
}
