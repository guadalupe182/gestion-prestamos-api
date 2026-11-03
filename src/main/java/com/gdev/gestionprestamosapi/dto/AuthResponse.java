package com.gdev.gestionprestamosapi.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn
) {
}