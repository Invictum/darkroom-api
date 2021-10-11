package com.github.darkroom;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darkroom.converters.CaseEntityToCaseResponse;
import com.github.darkroom.converters.CollectionEntityToTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // TODO: Possible move jackson config to separate class
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new CollectionEntityToTypeConverter());
        registry.addConverter(new CaseEntityToCaseResponse());
    }

    @PostConstruct
    public void configureMapper() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
