package com.github.darkroom.cases;

import com.github.darkroom.BrokerConfig;
import com.github.darkroom.cases.broker.ComparisonTask;
import com.github.darkroom.cases.controller.CaseMetadata;
import com.github.darkroom.cases.controller.CaseResponse;
import com.github.darkroom.cases.repository.CaseEntity;
import com.github.darkroom.cases.repository.CaseRepository;
import com.github.darkroom.classifiers.repository.ClassifierRepository;
import com.github.darkroom.collections.repository.CollectionRepository;
import com.github.darkroom.images.ImageType;
import com.github.darkroom.images.repository.ImageRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
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
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public CaseService(CollectionRepository collectionRepository, ClassifierRepository classifierRepository, CaseRepository caseRepository, ImageRepository imageRepository, ConversionService conversionService) {
        this.collectionRepository = collectionRepository;
        this.classifierRepository = classifierRepository;
        this.caseRepository = caseRepository;
        this.imageRepository = imageRepository;
        this.conversionService = conversionService;
    }

    public void saveCase(String collection, CaseMetadata metadata, MultipartFile image) {
        // Validate image
        var type = MediaType.valueOf(image.getContentType());
        if (!type.getType().equalsIgnoreCase("image")) {
            throw new InvalidMediaTypeException(type.toString(), "Only images are allowed");
        }
        var entity = saveCaseData(collection, metadata, image);
        // Send comparison task
        caseRepository.findTopByClassifier_IdAndBaseNotNull(entity.getClassifier().getId()).ifPresent(base -> {
            entity.setBase(base.getBase());
            caseRepository.save(entity);
            var path = base.getBase() + "/" + ImageType.ORIGINAL;
            var baseImage = imageRepository.load(path);
            try {
                var payload = new ComparisonTask(entity.getId(), baseImage.data(), image.getBytes());
                rabbitTemplate.convertAndSend(BrokerConfig.TASKS_EXCHANGE, "", payload);
            } catch (IOException e) {
                throw new RuntimeException("Unable to read sample image data", e);
            }
        });
    }

    @Transactional
    protected CaseEntity saveCaseData(String collection, CaseMetadata metadata, MultipartFile image) {
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
        try {
            imageRepository.save(imageUri, image.getContentType(), image.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return caseEntity;
    }

    public List<CaseResponse> loadRecent(String collection) {
        return convert(caseRepository.findAllLatestForCollection(collection));
    }

    public List<CaseResponse> loadSeries(String collection, String classifier) {
        return convert(caseRepository.findAllForCollectionAndClassifier(collection, classifier));
    }

    @Transactional
    public void saveComparisonResults(long id, byte[] image, int percentage) {
        caseRepository.findById(id).ifPresent(entity -> {
            var classifier = entity.getClassifier();
            // Build path
            var path = "%s/%s/%s".formatted(
                    classifier.getCollection().getId(),
                    classifier.getId(),
                    UUID.randomUUID()
            );
            // Save file to the storage
            var imageUri = path + "/" + ImageType.ORIGINAL;
            var stream = new ByteArrayInputStream(image);
            // TODO: Add Content type detection
            imageRepository.save(imageUri, "image/png", stream);
            // Update DB record
            entity.setDelta(path);
            entity.setDiffPercent(percentage);
            entity.setTimestamp(LocalDateTime.now());
            caseRepository.save(entity);
        });
    }

    @Transactional
    public void updateBase(String collection, String classifier, long id) {
        var iterable = caseRepository.findAllByClassifier_Collection_NameAndClassifier_Name(collection, classifier);
        var partitions = StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.partitioningBy(entity -> entity.getId() == id));
        if (partitions.get(true).isEmpty()) {
            // Nothing to do
            return;
        }
        var baseEntity = partitions.get(true).get(0);
        var base = baseEntity.getFile();
        var toUpdate = partitions.get(false);
        toUpdate.forEach(entity -> entity.setBase(base));
        caseRepository.saveAll(toUpdate);
        caseRepository.delete(baseEntity);
        // Send comparison tasks
        var baseData = imageRepository.load(base + "/" + ImageType.ORIGINAL).data();
        toUpdate.forEach(entity -> {
            var path = entity.getFile() + "/" + ImageType.ORIGINAL;
            var fileData = imageRepository.load(path).data();
            var payload = new ComparisonTask(entity.getId(), baseData, fileData);
            rabbitTemplate.convertAndSend(BrokerConfig.TASKS_EXCHANGE, "", payload);
        });
    }

    private List<CaseResponse> convert(Iterable<CaseEntity> entityIterable) {
        return StreamSupport.stream(entityIterable.spliterator(), false)
                .map(entity -> conversionService.convert(entity, CaseResponse.class))
                .collect(Collectors.toList());
    }
}
