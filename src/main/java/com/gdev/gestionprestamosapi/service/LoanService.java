package com.gdev.gestionprestamosapi.service;

import com.gdev.gestionprestamosapi.dto.LoanRequest;
import com.gdev.gestionprestamosapi.dto.LoanResponse;
import com.gdev.gestionprestamosapi.model.Client;
import com.gdev.gestionprestamosapi.model.Loan;
import com.gdev.gestionprestamosapi.model.LoanStatus;
import com.gdev.gestionprestamosapi.repository.ClientRepository;
import com.gdev.gestionprestamosapi.repository.LoanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LoanService {

    private static final BigDecimal DAYS_PER_YEAR = new BigDecimal("365");
    private static final int MONEY_SCALE = 2;

    private final LoanRepository loanRepository;
    private final ClientRepository clientRepository;

    public LoanService(LoanRepository loanRepository, ClientRepository clientRepository) {
        this.loanRepository = loanRepository;
        this.clientRepository = clientRepository;
    }

    public LoanResponse create(LoanRequest request) {
        validateDates(request.startDate(), request.endDate());
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        Loan loan = new Loan(client, request.amount(), request.tenantId());
        if (request.interestRate() != null) {
            loan.setInterestRate(request.interestRate());
        }
        if (request.startDate() != null) {
            loan.setStartDate(request.startDate());
        }
        if (request.endDate() != null) {
            loan.setEndDate(request.endDate());
        }
        if (request.status() != null) {
            loan.setStatus(request.status());
        }

        return toResponse(loanRepository.save(loan));
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> findAll() {
        return loanRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LoanResponse findById(UUID id) {
        return toResponse(getLoan(id));
    }

    public LoanResponse update(UUID id, LoanRequest request) {
        validateDates(request.startDate(), request.endDate());
        Loan loan = getLoan(id);

        if (!loan.getClient().getId().equals(request.clientId())) {
            Client client = clientRepository.findById(request.clientId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
            loan.setClient(client);
        }

        loan.setAmount(request.amount());
        if (request.interestRate() != null) {
            loan.setInterestRate(request.interestRate());
        }
        if (request.startDate() != null) {
            loan.setStartDate(request.startDate());
        }
        if (request.endDate() != null) {
            loan.setEndDate(request.endDate());
        }
        if (request.status() != null) {
            loan.setStatus(request.status());
        }
        loan.setTenantId(request.tenantId());

        return toResponse(loanRepository.save(loan));
    }

    public void delete(UUID id) {
        Loan loan = getLoan(id);
        loanRepository.delete(loan);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateInterest(UUID id) {
        return calculateInterest(id, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateInterest(UUID id, LocalDate asOfDate) {
        return calculateGeneratedInterest(id, asOfDate);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateGeneratedInterest(UUID id, LocalDate asOfDate) {
        Loan loan = getLoan(id);
        if (loan.getStatus() == LoanStatus.PAID) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }

        LocalDate effectiveEnd = asOfDate == null ? LocalDate.now() : asOfDate;
        if (loan.getEndDate() != null && loan.getEndDate().isBefore(effectiveEnd)) {
            effectiveEnd = loan.getEndDate();
        }

        if (effectiveEnd.isBefore(loan.getStartDate()) || effectiveEnd.isEqual(loan.getStartDate())) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }

        long days = ChronoUnit.DAYS.between(loan.getStartDate(), effectiveEnd);
        BigDecimal years = new BigDecimal(days).divide(DAYS_PER_YEAR, 10, RoundingMode.HALF_UP);
        return loan.getAmount()
                .multiply(loan.getInterestRate())
                .multiply(years)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private Loan getLoan(UUID id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado"));
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate no puede ser anterior a startDate");
        }
    }

    private LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getClient().getId(),
                loan.getAmount(),
                loan.getInterestRate(),
                loan.getStartDate(),
                loan.getEndDate(),
                loan.getStatus(),
                loan.getTenantId(),
                loan.getTotalToPay()
        );
    }
}