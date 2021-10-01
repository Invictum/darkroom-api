package com.github.darkroom.collections;

import com.github.darkroom.database.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CollectionService {

    private final CollectionRepository repository;
    private final ConversionService conversionService;

    @Autowired
    public CollectionService(CollectionRepository repository, ConversionService conversionService) {
        this.repository = repository;
        this.conversionService = conversionService;
    }

    public List<Collection> loadAll() {
        var spliterator = repository.findAll().spliterator();
        return StreamSupport.stream(spliterator, false)
                .map(item -> conversionService.convert(item, Collection.class))
                .collect(Collectors.toList());
    }
}
