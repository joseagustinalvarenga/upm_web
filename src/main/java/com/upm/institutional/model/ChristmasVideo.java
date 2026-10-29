package com.upm.institutional.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "christmas_videos")
@Data
public class ChristmasVideo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(max = 140)
    private String title;
    @Size(max = 500)
    private String description;
    @NotBlank @Size(max = 60)
    private String category;
    @NotBlank @Size(max = 1000)
    private String videoUrl;
    @Column(columnDefinition = "TEXT")
    private String cover;
    @NotNull @Min(0) @Max(9999)
    private Integer displayOrder = 0;
    private boolean visible = true;

    public String getPlatform() {
        return com.upm.institutional.service.ChristmasService.platform(videoUrl);
    }
}
