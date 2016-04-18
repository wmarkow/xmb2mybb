package vtech.xmb.grabber.db.xmb.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vtech.xmb.grabber.db.xmb.entities.XmbFavorites;
import vtech.xmb.grabber.db.xmb.entities.XmbForum;

@Repository
public interface XmbFavoritesRepository extends CrudRepository<XmbFavorites, Long> {
}
