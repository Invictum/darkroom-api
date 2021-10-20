package com.github.darkroom.cases.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    @Query(value = """
            SELECT ca.* FROM cases AS ca
                INNER JOIN classifiers AS cl ON ca.classifier_id = cl.id
                INNER JOIN collections c ON cl.collection_id = c.id
            WHERE c.name = :collection AND cl.name = :classifier
            ORDER BY ca.timestamp DESC
            """, nativeQuery = true)
    Iterable<CaseEntity> findAllForCollectionAndClassifier(@Param("collection") String collection, @Param("classifier") String classifier);

    Optional<CaseEntity> findTopByClassifier_IdAndBaseNotNull(long classifierId);

    Iterable<CaseEntity> findAllByClassifier_Collection_NameAndClassifier_Name(String collection, String classifier);
}
