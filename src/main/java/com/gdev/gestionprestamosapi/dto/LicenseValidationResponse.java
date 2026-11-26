package com.gdev.gestionprestamosapi.dto;

public record LicenseValidationResponse(
        boolean valid,
        String message
) {
}