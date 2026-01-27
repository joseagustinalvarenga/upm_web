package com.upm.institutional.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "features")
@Data
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String icon; // Bootstrap icon class (e.g. bi-award)
    private String title;
    private String description;

    @Column(name = "display_order")
    private Integer displayOrder;
}
