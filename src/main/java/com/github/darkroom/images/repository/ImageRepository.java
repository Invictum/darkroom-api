package com.github.darkroom.images.repository;

import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
public class ImageRepository {

    @Value("#{systemProperties['MINIO_BUCKET'] ?: 'darkroom'}")
    private String bucket;

    private final MinioClient minioClient;

    @Autowired
    public ImageRepository(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void createBucket() {
        var arguments = MakeBucketArgs.builder().bucket(bucket).build();
        try {
            minioClient.makeBucket(arguments);
        } catch (ErrorResponseException e) {
            // Bucket already exists, so do nothing
        } catch (MinioException | GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves particular file in the storage
     */
    public void save(String path, MultipartFile file) {
        try {
            var arguments = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getInputStream().available(), -1)
                    .build();
            minioClient.putObject(arguments);
        } catch (MinioException | GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ImageDetails load(String path) {
        var arguments = GetObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .build();
        try (var response = minioClient.getObject(arguments)) {
            return new ImageDetails(response.headers(), response.readAllBytes());
        } catch (MinioException | GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
