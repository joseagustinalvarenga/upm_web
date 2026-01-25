package com.upm.institutional.dto;

import com.upm.institutional.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class NewsDto {

    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 160, message = "El título debe tener entre 3 y 160 caracteres")
    private String title;

    @NotBlank(message = "El contenido es obligatorio")
    private String content;

    private String imageUrl;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    private Status status;
}
