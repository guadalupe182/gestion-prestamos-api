package com.gdev.gestionprestamosapi.controller;

import com.gdev.gestionprestamosapi.dto.PaymentRequest;
import com.gdev.gestionprestamosapi.dto.PaymentResponse;
import com.gdev.gestionprestamosapi.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loans/{loanId}/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@PathVariable UUID loanId,
                                                  @Valid @RequestBody PaymentRequest request) {
        return new ResponseEntity<>(paymentService.create(loanId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findByLoan(@PathVariable UUID loanId) {
        return ResponseEntity.ok(paymentService.findByLoan(loanId));
    }
}