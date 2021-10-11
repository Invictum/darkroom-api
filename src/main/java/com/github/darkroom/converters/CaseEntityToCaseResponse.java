package com.github.darkroom.converters;

import com.github.darkroom.cases.controller.CaseResponse;
import com.github.darkroom.cases.repository.CaseEntity;
import org.springframework.core.convert.converter.Converter;

public class CaseEntityToCaseResponse implements Converter<CaseEntity, CaseResponse> {

    @Override
    public CaseResponse convert(CaseEntity source) {
        return new CaseResponse(
                source.getId(),
                source.getClassifier().getName(),
                source.getFile(),
                source.getBase(),
                source.getDelta(),
                source.getDiffPercent(),
                source.getTimestamp()
        );
    }
}
