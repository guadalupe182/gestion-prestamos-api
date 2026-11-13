package com.gdev.gestionprestamosapi.service;

import com.gdev.gestionprestamosapi.dto.PaymentRequest;
import com.gdev.gestionprestamosapi.model.Client;
import com.gdev.gestionprestamosapi.model.Loan;
import com.gdev.gestionprestamosapi.model.LoanStatus;
import com.gdev.gestionprestamosapi.model.Payment;
import com.gdev.gestionprestamosapi.repository.LoanRepository;
import com.gdev.gestionprestamosapi.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void create_marksLoanAsPaidWhenFullyPaid() {
        UUID loanId = UUID.randomUUID();
        Client client = new Client("Ana", "ana@test.com");
        Loan loan = new Loan(client, new BigDecimal("1000.00"), "tenant-1");
        loan.setId(loanId);
        loan.setInterestRate(new BigDecimal("0.10"));

        Payment saved = new Payment(loan, new BigDecimal("1100.00"), LocalDate.now());
        saved.setId(UUID.randomUUID());

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);
        when(paymentRepository.findByLoanId(loanId)).thenReturn(List.of(saved));

        var response = paymentService.create(loanId, new PaymentRequest(new BigDecimal("1100.00"), LocalDate.now()));

        assertThat(response.amount()).isEqualByComparingTo("1100.00");
        verify(loanRepository).save(loan);
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.PAID);
    }

    @Test
    void create_keepsLoanActiveWhenPartialPayment() {
        UUID loanId = UUID.randomUUID();
        Client client = new Client("Ana", "ana@test.com");
        Loan loan = new Loan(client, new BigDecimal("1000.00"), "tenant-1");
        loan.setId(loanId);
        loan.setInterestRate(new BigDecimal("0.10"));

        Payment saved = new Payment(loan, new BigDecimal("500.00"), LocalDate.now());
        saved.setId(UUID.randomUUID());

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);
        when(paymentRepository.findByLoanId(loanId)).thenReturn(List.of(saved));

        paymentService.create(loanId, new PaymentRequest(new BigDecimal("500.00"), LocalDate.now()));

        assertThat(loan.getStatus()).isEqualTo(LoanStatus.ACTIVE);
    }

    @Test
    void findByLoan_returnsPayments() {
        UUID loanId = UUID.randomUUID();
        Client client = new Client("Ana", "ana@test.com");
        Loan loan = new Loan(client, new BigDecimal("1000.00"), "tenant-1");
        loan.setId(loanId);

        Payment payment = new Payment(loan, new BigDecimal("100.00"), LocalDate.now());
        payment.setId(UUID.randomUUID());

        when(loanRepository.existsById(loanId)).thenReturn(true);
        when(paymentRepository.findByLoanId(loanId)).thenReturn(List.of(payment));

        var payments = paymentService.findByLoan(loanId);

        assertThat(payments).hasSize(1);
    }
}