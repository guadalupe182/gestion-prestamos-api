package com.gdev.gestionprestamosapi.dto;

import com.gdev.gestionprestamosapi.model.LoanStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LoanRequest(
        @NotNull UUID clientId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        BigDecimal interestRate,
        LocalDate startDate,
        LocalDate endDate,
        LoanStatus status,
        @NotBlank String tenantId
) {
}