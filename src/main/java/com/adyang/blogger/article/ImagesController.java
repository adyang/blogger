package com.adyang.blogger.article;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/images")
public class ImagesController {
    static final String IMAGE_DIR = System.getProperty("java.io.tmpdir");

    private ImageRepository imageRepository;
    private ResourceLoader resourceLoader;

    @Autowired
    public ImagesController(ImageRepository imageRepository, ResourceLoader resourceLoader) {
        this.imageRepository = imageRepository;
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> showImage(@PathVariable Long id) {
        Optional<Image> foundImage = imageRepository.findById(id);
        Image image = foundImage.orElseThrow(ImageNotFound::new);
        Resource imageResource = resourceLoader.getResource(locationOf(image));
        return trySendingResponse(image, imageResource);
    }

    private String locationOf(Image image) {
        return "file:" + IMAGE_DIR + "/" + image.getFileName();
    }

    private ResponseEntity<?> trySendingResponse(Image image, Resource imageResource) {
        try {
            return ResponseEntity.ok()
                    .contentLength(image.getFileSize())
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new InputStreamResource(imageResource.getInputStream()));
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private static class ImageNotFound extends RuntimeException {
    }
}
