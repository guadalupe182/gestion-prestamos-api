package com.gdev.gestionprestamosapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidad base del MVP para los préstamos.
 * El interés fijo del producto es del 10%.
 */
@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
public class Loan {

    public static final BigDecimal INTEREST_RATE = new BigDecimal("0.10");

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 5, scale = 4)
    @Setter(AccessLevel.NONE)
    private BigDecimal interestRate = INTEREST_RATE;

    @Column(nullable = false)
    private LocalDate startDate = LocalDate.now();

    @Column
    private LocalDate endDate;

    @Column(nullable = false, length = 100)
    private String tenantId;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LoanStatus status = LoanStatus.ACTIVE;

    public Loan(Client client, BigDecimal amount) {
        this.client = client;
        this.amount = amount;
        this.startDate = LocalDate.now();
    }

    public Loan(Client client, BigDecimal amount, String tenantId) {
        this.client = client;
        this.amount = amount;
        this.tenantId = tenantId;
        this.startDate = LocalDate.now();
    }

    @PrePersist
    void ensureBusinessRules() {
        if (interestRate == null) {
            interestRate = INTEREST_RATE;
        }
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = LoanStatus.ACTIVE;
        }
    }

    public void setInterestRate(BigDecimal interestRate) {
        if (interestRate != null) {
            this.interestRate = interestRate;
        }
    }

    public BigDecimal getTotalToPay() {
        return amount.add(amount.multiply(interestRate));
    }

    public BigDecimal getTotalAmount() {
        return getTotalToPay();
    }

    public BigDecimal calculateTotalAmount() {
        return getTotalToPay();
    }
}