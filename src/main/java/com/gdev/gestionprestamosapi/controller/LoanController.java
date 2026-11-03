package com.gdev.gestionprestamosapi.controller;

import com.gdev.gestionprestamosapi.dto.LoanRequest;
import com.gdev.gestionprestamosapi.dto.LoanResponse;
import com.gdev.gestionprestamosapi.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<LoanResponse> create(@Valid @RequestBody LoanRequest request) {
        return new ResponseEntity<>(loanService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> findAll() {
        return ResponseEntity.ok(loanService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanResponse> update(@PathVariable UUID id, @Valid @RequestBody LoanRequest request) {
        return ResponseEntity.ok(loanService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        loanService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/calculate-interest")
    public ResponseEntity<Map<String, Object>> calculateInterest(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of(
                "loanId", id,
                "interest", loanService.calculateInterest(id)
        ));
    }
}