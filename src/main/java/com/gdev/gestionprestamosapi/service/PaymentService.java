package com.gdev.gestionprestamosapi.service;

import com.gdev.gestionprestamosapi.dto.PaymentRequest;
import com.gdev.gestionprestamosapi.dto.PaymentResponse;
import com.gdev.gestionprestamosapi.model.Loan;
import com.gdev.gestionprestamosapi.model.LoanStatus;
import com.gdev.gestionprestamosapi.model.Payment;
import com.gdev.gestionprestamosapi.repository.LoanRepository;
import com.gdev.gestionprestamosapi.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LoanRepository loanRepository;

    public PaymentService(PaymentRepository paymentRepository, LoanRepository loanRepository) {
        this.paymentRepository = paymentRepository;
        this.loanRepository = loanRepository;
    }

    public PaymentResponse create(UUID loanId, PaymentRequest request) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado"));

        Payment payment = new Payment(loan, request.amount(), request.paymentDate());
        Payment saved = paymentRepository.save(payment);

        BigDecimal totalPaid = paymentRepository.findByLoanId(loanId).stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPaid.compareTo(loan.getTotalToPay()) >= 0) {
            loan.setStatus(LoanStatus.PAID);
            loanRepository.save(loan);
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findByLoan(UUID loanId) {
        if (!loanRepository.existsById(loanId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado");
        }
        return paymentRepository.findByLoanId(loanId).stream().map(this::toResponse).toList();
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getLoan().getId(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getCreatedAt()
        );
    }
}