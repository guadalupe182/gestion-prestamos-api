package com.gdev.gestionprestamosapi.repository;

import com.gdev.gestionprestamosapi.model.Client;
import com.gdev.gestionprestamosapi.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    List<Loan> findByClient(Client client);

    List<Loan> findByClientId(UUID clientId);

    List<Loan> findByTenantId(String tenantId);

    List<Loan> findByStatus(com.gdev.gestionprestamosapi.model.LoanStatus status);
}