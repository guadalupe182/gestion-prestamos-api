package com.gdev.gestionprestamosapi.service;

import com.gdev.gestionprestamosapi.model.Client;
import com.gdev.gestionprestamosapi.model.Loan;
import com.gdev.gestionprestamosapi.model.LoanStatus;
import com.gdev.gestionprestamosapi.repository.ClientRepository;
import com.gdev.gestionprestamosapi.repository.LoanRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private LoanService loanService;

    @BeforeEach
    void setUpSecurity() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("tenant-1", null,
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))));
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

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

    @Test
    void findById_ForbiddenWhenLoanBelongsToAnotherTenant() {
        UUID loanId = UUID.randomUUID();
        Client client = new Client("Ana", "ana@test.com");
        Loan loan = new Loan(client, new BigDecimal("1000.00"), "other-tenant");
        loan.setId(loanId);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.findById(loanId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403");
    }
}
