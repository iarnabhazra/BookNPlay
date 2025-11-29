package com.booknplay.payment.api;

import com.booknplay.payment.domain.Payment;
import com.booknplay.payment.service.PaymentProcessingService;
import com.booknplay.payment.domain.PaymentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentProcessingService service;
    private final PaymentRepository repository;

    public PaymentController(PaymentProcessingService service, PaymentRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Payment> pay(@Valid @RequestBody PaymentRequest request) throws ExecutionException, InterruptedException {
        Payment p = service.initiate(
                request.bookingRef(),
                request.baseAmount(),
                request.demandFactor(),
                request.slot()
        ).get();
        return ResponseEntity.ok(p);
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(repository.findAll());
    }

    // Immutable request DTO using Java record; Jackson supports record components.
    record PaymentRequest(
            @NotBlank String bookingRef,
            @NotNull @DecimalMin(value = "0.00", inclusive = true) BigDecimal baseAmount,
            @Min(0) @Max(100) int demandFactor,
            @NotNull ZonedDateTime slot
    ) {}
}
