package com.upm.institutional.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "carousel_images")
@Data
@NoArgsConstructor
public class CarouselImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_data", columnDefinition = "TEXT", nullable = false)
    private String imageData;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
