package com.github.darkroom.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionRepository extends CrudRepository<CollectionEntity, Long> {

    Optional<CollectionEntity> findByName(String name);

    default CollectionEntity loadExistingOrCreate(String name) {
        return findByName(name).orElseGet(() -> {
            var entity = new CollectionEntity();
            entity.setName(name);
            return this.save(entity);
        });
    }
}
