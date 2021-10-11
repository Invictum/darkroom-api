package com.github.darkroom.cases;

import com.github.darkroom.cases.controller.CaseMetadata;
import com.github.darkroom.cases.controller.CaseResponse;
import com.github.darkroom.cases.repository.CaseEntity;
import com.github.darkroom.cases.repository.CaseRepository;
import com.github.darkroom.classifiers.repository.ClassifierRepository;
import com.github.darkroom.collections.repository.CollectionRepository;
import com.github.darkroom.images.ImageType;
import com.github.darkroom.images.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CaseService {

    private final CollectionRepository collectionRepository;
    private final ClassifierRepository classifierRepository;
    private final CaseRepository caseRepository;
    private final ImageRepository imageRepository;
    private final ConversionService conversionService;

    @Autowired
    public CaseService(CollectionRepository collectionRepository, ClassifierRepository classifierRepository, CaseRepository caseRepository, ImageRepository imageRepository, ConversionService conversionService) {
        this.collectionRepository = collectionRepository;
        this.classifierRepository = classifierRepository;
        this.caseRepository = caseRepository;
        this.imageRepository = imageRepository;
        this.conversionService = conversionService;
    }

    @Transactional
    public void saveCase(String collection, CaseMetadata metadata, MultipartFile image) {
        // Validate image
        var type = MediaType.valueOf(image.getContentType());
        if (!type.getType().equalsIgnoreCase("image")) {
            throw new InvalidMediaTypeException(type.toString(), "Only images are allowed");
        }
        // Save to the database
        var collectionEntity = collectionRepository.loadExistingOrCreate(collection);
        var classifierEntity = classifierRepository.loadExistingOrCreate(metadata.classifier());
        classifierEntity.setCollection(collectionEntity);
        var caseEntity = new CaseEntity();
        caseEntity.setClassifier(classifierEntity);
        var path = "%s/%s/%s".formatted(
                collectionEntity.getId(),
                classifierEntity.getId(),
                UUID.randomUUID()
        );
        caseEntity.setFile(path);
        caseRepository.save(caseEntity);
        // Save to the file storage
        var imageUri = path + "/" + ImageType.ORIGINAL;
        imageRepository.save(imageUri, image);
    }

    public List<CaseResponse> loadRecent(String collection) {
        return convert(caseRepository.findAllLatestForCollection(collection));
    }

    public List<CaseResponse> loadSeries(String collection, String classifier) {
        return convert(caseRepository.findAllForCollectionAndClassifier(collection, classifier));
    }

    private List<CaseResponse> convert(Iterable<CaseEntity> entityIterable) {
        return StreamSupport.stream(entityIterable.spliterator(), false)
                .map(entity -> conversionService.convert(entity, CaseResponse.class))
                .collect(Collectors.toList());
    }
}
