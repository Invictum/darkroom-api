package com.github.darkroom.database;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseRepository extends CrudRepository<CaseEntity, Long> {

    @Query(value = """
            SELECT DISTINCT ON (cl.name) ca.* FROM cases AS ca
                INNER JOIN classifiers cl ON ca.classifier_id = cl.id
                INNER JOIN collections c ON cl.collection_id = c.id
            WHERE c.name = :collection
            ORDER BY cl.name, ca.timestamp DESC
            """, nativeQuery = true)
    Iterable<CaseEntity> findAllLatestForCollection(@Param("collection") String collection);
}
