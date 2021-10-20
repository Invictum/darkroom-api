package com.github.darkroom.classifiers.repository;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface ClassifierRepository extends CrudRepository<ClassifierEntity, Long> {

    Optional<ClassifierEntity> findByName(String name);

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    default ClassifierEntity loadExistingOrCreate(String name) {
        return findByName(name).orElseGet(() -> {
            var entity = new ClassifierEntity();
            entity.setName(name);
            return save(entity);
        });
    }
}
