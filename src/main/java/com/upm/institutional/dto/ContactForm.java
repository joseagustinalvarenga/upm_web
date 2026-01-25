package com.upm.institutional.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactForm {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
    private String fullName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe proporcionar un email válido")
    private String email;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(min = 3, max = 160, message = "El asunto debe tener entre 3 y 160 caracteres")
    private String subject;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 10, max = 4000, message = "El mensaje debe tener entre 10 y 4000 caracteres")
    private String message;
}
