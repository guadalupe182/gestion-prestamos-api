package com.gdev.gestionprestamosapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Payment(Loan loan, BigDecimal amount, LocalDate paymentDate) {
        this.loan = loan;
        this.amount = amount;
        this.paymentDate = paymentDate == null ? LocalDate.now() : paymentDate;
    }

    @PrePersist
    void ensureDefaults() {
        if (paymentDate == null) {
            paymentDate = LocalDate.now();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}