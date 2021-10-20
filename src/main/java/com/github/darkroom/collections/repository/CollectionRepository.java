package com.github.darkroom.collections.repository;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface CollectionRepository extends CrudRepository<CollectionEntity, Long> {

    Optional<CollectionEntity> findByName(String name);

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    default CollectionEntity loadExistingOrCreate(String name) {
        return findByName(name).orElseGet(() -> {
            var entity = new CollectionEntity();
            entity.setName(name);
            return this.save(entity);
        });
    }
}
