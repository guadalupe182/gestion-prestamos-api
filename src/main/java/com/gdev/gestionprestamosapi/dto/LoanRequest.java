package com.gdev.gestionprestamosapi.dto;

import com.gdev.gestionprestamosapi.model.LoanStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LoanRequest(
        @NotNull(message = "El cliente es obligatorio") UUID clientId,
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero") BigDecimal amount,
        BigDecimal interestRate,
        LocalDate startDate,
        LocalDate endDate,
        LoanStatus status,
        @NotBlank(message = "El tenant es obligatorio") String tenantId
) {
}