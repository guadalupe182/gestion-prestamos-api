package com.gdev.gestionprestamosapi.dto;

import com.gdev.gestionprestamosapi.model.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LoanResponse(
        UUID id,
        UUID clientId,
        BigDecimal amount,
        BigDecimal interestRate,
        LocalDate startDate,
        LocalDate endDate,
        LoanStatus status,
        String tenantId,
        BigDecimal totalToPay
) {
}