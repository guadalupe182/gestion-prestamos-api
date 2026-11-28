package com.gdev.gestionprestamosapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre de usuario debe tener entre 3 y 100 caracteres")
        String username,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password
) {
}