package com.github.darkroom.images.controller;

import com.github.darkroom.images.ImageService;
import com.github.darkroom.images.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(path = "images/**")
    public ResponseEntity<?> getImage(@RequestParam("type") ImageType type, HttpServletRequest request) {
        var path = request.getRequestURI().split(request.getContextPath() + "/images/")[1];
        // TODO: Return placeholder for not found images
        var details = imageService.loadImageForPath(path, type);
        var headers = new HttpHeaders();
        details.headers().forEach(pair -> headers.add(pair.getFirst(), pair.getSecond()));
        return new ResponseEntity<>(details.data(), headers, HttpStatus.OK);
    }
}
