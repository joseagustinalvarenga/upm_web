package com.upm.institutional.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "academic_offers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String area;

    @Column(nullable = false)
    private String course;

    @Column(name = "class_count", nullable = false)
    private String classCount;
}
