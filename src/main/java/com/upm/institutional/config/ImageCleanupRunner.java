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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageCleanupRunner implements CommandLineRunner {

    private final NewsRepository newsRepository;
    private final CourseRepository courseRepository;
    private final CarouselImageRepository carouselImageRepository;

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

    @Transactional
    public void optimizeNewsImages() {
        List<News> newsList = newsRepository.findAll();
        int updatedCount = 0;
        for (News news : newsList) {
            String imgUrl = news.getImageUrl();
            if (imgUrl != null && imgUrl.startsWith("data:") && imgUrl.length() > 100000) { // > ~75KB in base64
                log.info("Optimizing News ID: {}, current base64 length: {}", news.getId(), imgUrl.length());
                String optimized = ImageUtils.resizeAndCompressBase64(imgUrl, 1024, 0.75f);
                if (optimized != null && optimized.length() < imgUrl.length()) {
                    news.setImageUrl(optimized);
                    newsRepository.save(news);
                    updatedCount++;
                    log.info("Optimized News ID: {} -> new base64 length: {} (Reduced by {}%)",
                            news.getId(), optimized.length(), (100 - (optimized.length() * 100L / imgUrl.length())));
                }
            }
        }
        if (updatedCount > 0) {
            log.info("Successfully optimized {} News images.", updatedCount);
        }
    }

    @Transactional
    public void optimizeCourseImages() {
        List<Course> courses = courseRepository.findAll();
        int updatedCount = 0;
        for (Course course : courses) {
            String imgUrl = course.getImageUrl();
            if (imgUrl != null && imgUrl.startsWith("data:") && imgUrl.length() > 100000) {
                log.info("Optimizing Course ID: {}, current base64 length: {}", course.getId(), imgUrl.length());
                String optimized = ImageUtils.resizeAndCompressBase64(imgUrl, 1024, 0.75f);
                if (optimized != null && optimized.length() < imgUrl.length()) {
                    course.setImageUrl(optimized);
                    courseRepository.save(course);
                    updatedCount++;
                    log.info("Optimized Course ID: {} -> new base64 length: {} (Reduced by {}%)",
                            course.getId(), optimized.length(), (100 - (optimized.length() * 100L / imgUrl.length())));
                }
            }
        }
        if (updatedCount > 0) {
            log.info("Successfully optimized {} Course images.", updatedCount);
        }
    }

    @Transactional
    public void optimizeCarouselImages() {
        List<CarouselImage> images = carouselImageRepository.findAll();
        int updatedCount = 0;
        for (CarouselImage image : images) {
            String imgData = image.getImageData();
            if (imgData != null && imgData.startsWith("data:") && imgData.length() > 100000) {
                log.info("Optimizing CarouselImage ID: {}, current base64 length: {}", image.getId(), imgData.length());
                String optimized = ImageUtils.resizeAndCompressBase64(imgData, 1280, 0.75f); // Carousel can be slightly wider, e.g. 1280
                if (optimized != null && optimized.length() < imgData.length()) {
                    image.setImageData(optimized);
                    carouselImageRepository.save(image);
                    updatedCount++;
                    log.info("Optimized CarouselImage ID: {} -> new base64 length: {} (Reduced by {}%)",
                            image.getId(), optimized.length(), (100 - (optimized.length() * 100L / imgData.length())));
                }
            }
        }
        if (updatedCount > 0) {
            log.info("Successfully optimized {} Carousel images.", updatedCount);
        }
    }
}
