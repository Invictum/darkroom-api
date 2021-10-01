package com.github.darkroom.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassifierRepository extends CrudRepository<ClassifierEntity, Long> {

    Optional<ClassifierEntity> findByName(String name);

    default ClassifierEntity loadExistingOrCreate(String name) {
        return findByName(name).orElseGet(() -> {
            var entity = new ClassifierEntity();
            entity.setName(name);
            return save(entity);
        });
    }
}
