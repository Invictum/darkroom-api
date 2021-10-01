package com.github.darkroom.cases;

import com.github.darkroom.database.CaseEntity;
import com.github.darkroom.database.CaseRepository;
import com.github.darkroom.database.ClassifierRepository;
import com.github.darkroom.database.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CaseService {

    private final CollectionRepository collectionRepository;
    private final ClassifierRepository classifierRepository;
    private final CaseRepository caseRepository;

    @Autowired
    public CaseService(CollectionRepository collectionRepository, ClassifierRepository classifierRepository, CaseRepository caseRepository) {
        this.collectionRepository = collectionRepository;
        this.classifierRepository = classifierRepository;
        this.caseRepository = caseRepository;
    }

    @Transactional
    public void saveCaseToDatabase(String collection, String classifier) {
        var collectionEntity = collectionRepository.loadExistingOrCreate(collection);
        var classifierEntity = classifierRepository.loadExistingOrCreate(classifier);
        classifierEntity.setCollection(collectionEntity);
        var caseEntity = new CaseEntity();
        caseEntity.setClassifier(classifierEntity);
        caseEntity.setFile("base/path");
        caseRepository.save(caseEntity);
    }

    public void saveCase(String collection, CaseMetadata metadata, MultipartFile image) {
        saveCaseToDatabase(collection, metadata.classifier());
        // TODO: Save to file storage here
    }
}
