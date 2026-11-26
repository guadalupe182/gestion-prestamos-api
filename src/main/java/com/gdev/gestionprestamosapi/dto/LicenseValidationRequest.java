package com.gdev.gestionprestamosapi.dto;

import jakarta.validation.constraints.NotBlank;

public record LicenseValidationRequest(
        @NotBlank String licenseKey
) {
}