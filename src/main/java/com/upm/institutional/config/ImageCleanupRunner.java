package com.upm.institutional.config;

import com.upm.institutional.model.CarouselImage;
import com.upm.institutional.model.Course;
import com.upm.institutional.model.News;
import com.upm.institutional.repository.CarouselImageRepository;
import com.upm.institutional.repository.CourseRepository;
import com.upm.institutional.repository.NewsRepository;
import com.upm.institutional.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageCleanupRunner implements CommandLineRunner {

    private final NewsRepository newsRepository;
    private final CourseRepository courseRepository;
    private final CarouselImageRepository carouselImageRepository;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting database image size audit and optimization...");
        try {
            optimizeNewsImages();
            optimizeCourseImages();
            optimizeCarouselImages();
            log.info("Database image optimization completed successfully!");
        } catch (Exception e) {
            log.error("Error running database image optimization: ", e);
        }
    }

    public void optimizeNewsImages() {
        List<Long> ids = newsRepository.findAllIds();
        log.info("Found {} News items to audit", ids.size());
        int updatedCount = 0;
        for (Long id : ids) {
            Boolean updated = transactionTemplate.execute(status -> {
                News news = newsRepository.findById(id).orElse(null);
                if (news != null) {
                    String imgUrl = news.getImageUrl();
                    if (imgUrl != null && imgUrl.startsWith("data:") && imgUrl.length() > 100000) {
                        log.info("Optimizing News ID: {}, current base64 length: {}", id, imgUrl.length());
                        String optimized = ImageUtils.resizeAndCompressBase64(imgUrl, 1024, 0.75f);
                        if (optimized != null && optimized.length() < imgUrl.length()) {
                            news.setImageUrl(optimized);
                            newsRepository.save(news);
                            log.info("Optimized News ID: {} -> new base64 length: {} (Reduced by {}%)",
                                    id, optimized.length(), (100 - (optimized.length() * 100L / imgUrl.length())));
                            return true;
                        }
                    }
                }
                return false;
            });
            if (Boolean.TRUE.equals(updated)) {
                updatedCount++;
            }
        }
        if (updatedCount > 0) {
            log.info("Successfully optimized {} News images.", updatedCount);
        }
    }

    public void optimizeCourseImages() {
        List<Long> ids = courseRepository.findAllIds();
        log.info("Found {} Course items to audit", ids.size());
        int updatedCount = 0;
        for (Long id : ids) {
            Boolean updated = transactionTemplate.execute(status -> {
                Course course = courseRepository.findById(id).orElse(null);
                if (course != null) {
                    String imgUrl = course.getImageUrl();
                    if (imgUrl != null && imgUrl.startsWith("data:") && imgUrl.length() > 100000) {
                        log.info("Optimizing Course ID: {}, current base64 length: {}", id, imgUrl.length());
                        String optimized = ImageUtils.resizeAndCompressBase64(imgUrl, 1024, 0.75f);
                        if (optimized != null && optimized.length() < imgUrl.length()) {
                            course.setImageUrl(optimized);
                            courseRepository.save(course);
                            log.info("Optimized Course ID: {} -> new base64 length: {} (Reduced by {}%)",
                                    id, optimized.length(), (100 - (optimized.length() * 100L / imgUrl.length())));
                            return true;
                        }
                    }
                }
                return false;
            });
            if (Boolean.TRUE.equals(updated)) {
                updatedCount++;
            }
        }
        if (updatedCount > 0) {
            log.info("Successfully optimized {} Course images.", updatedCount);
        }
    }

    public void optimizeCarouselImages() {
        List<Long> ids = carouselImageRepository.findAllIds();
        log.info("Found {} CarouselImage items to audit", ids.size());
        int updatedCount = 0;
        for (Long id : ids) {
            Boolean updated = transactionTemplate.execute(status -> {
                CarouselImage image = carouselImageRepository.findById(id).orElse(null);
                if (image != null) {
                    String imgData = image.getImageData();
                    if (imgData != null && imgData.startsWith("data:") && imgData.length() > 100000) {
                        log.info("Optimizing CarouselImage ID: {}, current base64 length: {}", id, imgData.length());
                        String optimized = ImageUtils.resizeAndCompressBase64(imgData, 1280, 0.75f);
                        if (optimized != null && optimized.length() < imgData.length()) {
                            image.setImageData(optimized);
                            carouselImageRepository.save(image);
                            log.info("Optimized CarouselImage ID: {} -> new base64 length: {} (Reduced by {}%)",
                                    id, optimized.length(), (100 - (optimized.length() * 100L / imgData.length())));
                            return true;
                        }
                    }
                }
                return false;
            });
            if (Boolean.TRUE.equals(updated)) {
                updatedCount++;
            }
        }
        if (updatedCount > 0) {
            log.info("Successfully optimized {} Carousel images.", updatedCount);
        }
    }
}
