package com.upm.institutional.service;

import com.upm.institutional.dto.NewsDto;
import com.upm.institutional.model.News;
import com.upm.institutional.model.Status;
import com.upm.institutional.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public Page<News> findPublishedNews(Pageable pageable) {
        return newsRepository.findByStatus(Status.PUBLISHED, pageable);
    }

    public Page<News> findAllNews(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }

    public News findById(Long id) {
        return newsRepository.findById(id).orElseThrow(() -> new RuntimeException("Noticia no encontrada"));
    }

    public News findPublishedById(Long id) {
        News news = findById(id);
        if (news.getStatus() != Status.PUBLISHED) {
            throw new RuntimeException("Noticia no disponible");
        }
        return news;
    }

    @Transactional
    public void saveNews(NewsDto newsDto) {
        News news = new News();
        if (newsDto.getId() != null) {
            news = findById(newsDto.getId());
        } else {
            news.setStatus(Status.DRAFT);
        }
        news.setTitle(newsDto.getTitle());
        news.setContent(newsDto.getContent());
        
        if (newsDto.getImageFile() != null && !newsDto.getImageFile().isEmpty()) {
            try {
                String base64Image = Base64.getEncoder().encodeToString(newsDto.getImageFile().getBytes());
                String mimeType = newsDto.getImageFile().getContentType();
                String rawDataUri = "data:" + mimeType + ";base64," + base64Image;
                String optimizedDataUri = com.upm.institutional.util.ImageUtils.resizeAndCompressBase64(rawDataUri, 1024, 0.75f);
                news.setImageUrl(optimizedDataUri);
            } catch (IOException e) {
                throw new RuntimeException("Error al procesar la imagen", e);
            }
        } else if (newsDto.getImageUrl() != null) {
            if (newsDto.getImageUrl().startsWith("data:")) {
                news.setImageUrl(com.upm.institutional.util.ImageUtils.resizeAndCompressBase64(newsDto.getImageUrl(), 1024, 0.75f));
            } else {
                news.setImageUrl(newsDto.getImageUrl());
            }
        }

        news.setEventDate(newsDto.getEventDate());
        if (newsDto.getStatus() != null) {
            news.setStatus(newsDto.getStatus());
        }
        newsRepository.save(news);
    }

    @Transactional
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }

    @Transactional
    public void changeStatus(Long id, Status status) {
        News news = findById(id);
        news.setStatus(status);
        newsRepository.save(news);
    }

    public NewsDto toDto(News news) {
        NewsDto dto = new NewsDto();
        dto.setId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setContent(news.getContent());
        dto.setImageUrl(news.getImageUrl());
        dto.setEventDate(news.getEventDate());
        dto.setStatus(news.getStatus());
        return dto;
    }
}
