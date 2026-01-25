package com.upm.institutional.dto;

import com.upm.institutional.model.Modality;
import com.upm.institutional.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CourseDto {

    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 120, message = "El título debe tener entre 3 y 120 caracteres")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotNull(message = "La modalidad es obligatoria")
    private Modality modality;

    @Size(max = 50, message = "La duración no puede exceder 50 caracteres")
    private String duration;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    private Status status;

    private String imageUrl;
}
