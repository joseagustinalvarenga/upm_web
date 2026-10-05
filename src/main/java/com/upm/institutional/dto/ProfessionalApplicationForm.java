package com.upm.institutional.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfessionalApplicationForm {

    @NotBlank(message = "El nombre y apellido son obligatorios")
    @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
    private String fullName;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 6, max = 20, message = "Ingrese un DNI válido")
    private String dni;

    @NotBlank(message = "La profesión u oficio es obligatorio")
    @Size(min = 2, max = 150, message = "La profesión debe tener entre 2 y 150 caracteres")
    private String profession;

    @NotBlank(message = "El teléfono de contacto es obligatorio")
    @Size(min = 6, max = 50, message = "Ingrese un número de teléfono válido")
    private String phone;

    @NotBlank(message = "El email de contacto es obligatorio")
    @Email(message = "Debe proporcionar un email válido")
    private String email;

    @NotBlank(message = "La localidad es obligatoria")
    @Size(min = 2, max = 100, message = "Ingrese una localidad válida")
    private String locality;

    @Size(max = 255, message = "El curso/egreso no debe superar 255 caracteres")
    private String courseCompleted;

    @Size(max = 2000, message = "Las observaciones no deben superar los 2000 caracteres")
    private String notes;
}
