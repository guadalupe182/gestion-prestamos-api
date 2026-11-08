package com.gdev.gestionprestamosapi.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID loanId,
        BigDecimal amount,
        LocalDate paymentDate,
        Instant createdAt
) {
}