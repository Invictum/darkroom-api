package com.github.darkroom.images;

import com.github.darkroom.images.repository.ImageDetails;
import com.github.darkroom.images.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public ImageDetails loadImageForPath(String path, ImageType type) {
        var fullPath = path + "/" + type;
        return imageRepository.load(fullPath);
    }
}
