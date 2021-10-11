package com.github.darkroom.converters;

import com.github.darkroom.collections.controller.Collection;
import com.github.darkroom.collections.repository.CollectionEntity;
import org.springframework.core.convert.converter.Converter;

public class CollectionEntityToTypeConverter implements Converter<CollectionEntity, Collection> {

    @Override
    public Collection convert(CollectionEntity source) {
        return new Collection(source.getId(), source.getName());
    }
}
