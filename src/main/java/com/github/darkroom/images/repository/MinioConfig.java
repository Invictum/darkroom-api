package com.github.darkroom.images.repository;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("#{systemProperties['MINIO_ENDPOINT'] ?: 'http://localhost:9000'}")
    private String endpoint;

    @Value("#{systemProperties['MINIO_ACCESS_KEY'] ?: 'minio'}")
    private String accessKey;

    @Value("#{systemProperties['MINIO_SECRET_KEY'] ?: 'minio-password'}")
    private String secretKey;

    @Bean
    public MinioClient client() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
