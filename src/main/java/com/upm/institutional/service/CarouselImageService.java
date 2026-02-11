package com.upm.institutional.service;

import com.upm.institutional.model.CarouselImage;
import com.upm.institutional.repository.CarouselImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarouselImageService {

    private final CarouselImageRepository carouselImageRepository;

    public List<CarouselImage> findAll() {
        return carouselImageRepository.findAll();
    }

    @Transactional
    public void uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String mimeType = file.getContentType();

            CarouselImage image = new CarouselImage();
            image.setImageData("data:" + mimeType + ";base64," + base64Image);

            carouselImageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("Error al procesar la imagen", e);
        }
    }

    @Transactional
    public void deleteImage(Long id) {
        carouselImageRepository.deleteById(id);
    }
}
