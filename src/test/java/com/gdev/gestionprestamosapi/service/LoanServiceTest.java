package com.gdev.gestionprestamosapi.service;

import com.gdev.gestionprestamosapi.model.Client;
import com.gdev.gestionprestamosapi.model.Loan;
import com.gdev.gestionprestamosapi.model.LoanStatus;
import com.gdev.gestionprestamosapi.repository.ClientRepository;
import com.gdev.gestionprestamosapi.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    void calculateInterest_generatesSimpleInterestForActiveLoan() {
        UUID loanId = UUID.randomUUID();
        Client client = new Client("Ana", "ana@test.com");
        Loan loan = new Loan(client, new BigDecimal("1000.00"), "tenant-1");
        loan.setId(loanId);
        loan.setStartDate(LocalDate.now().minusDays(365));
        loan.setInterestRate(new BigDecimal("0.10"));
        loan.setStatus(LoanStatus.ACTIVE);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        BigDecimal interest = loanService.calculateInterest(loanId, LocalDate.now());

        assertThat(interest).isEqualByComparingTo("100.00");
    }

    @Test
    void calculateInterest_returnsZeroForPaidLoan() {
        UUID loanId = UUID.randomUUID();
        Client client = new Client("Ana", "ana@test.com");
        Loan loan = new Loan(client, new BigDecimal("1000.00"), "tenant-1");
        loan.setId(loanId);
        loan.setStartDate(LocalDate.now().minusDays(365));
        loan.setStatus(LoanStatus.PAID);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        BigDecimal interest = loanService.calculateInterest(loanId, LocalDate.now());

        assertThat(interest).isEqualByComparingTo("0.00");
    }

    @Test
    void calculateInterest_capsAccrualAtEndDate() {
        UUID loanId = UUID.randomUUID();
        Client client = new Client("Ana", "ana@test.com");
        Loan loan = new Loan(client, new BigDecimal("1000.00"), "tenant-1");
        loan.setId(loanId);
        loan.setStartDate(LocalDate.now().minusDays(365));
        loan.setEndDate(LocalDate.now().minusDays(180));
        loan.setStatus(LoanStatus.ACTIVE);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        BigDecimal interest = loanService.calculateInterest(loanId, LocalDate.now());

        assertThat(interest).isLessThan(new BigDecimal("100.00"));
    }
}
