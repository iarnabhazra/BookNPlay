package com.booknplay.payment.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bookingRef;
    private BigDecimal amount;
    private String currency;
    private String status; // INITIATED, SUCCESS, FAILED
    private Instant createdAt;

    protected Payment() {}

    public Payment(String bookingRef, BigDecimal amount, String currency, String status, Instant createdAt) {
        this.bookingRef = bookingRef;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Payment createInitiated(String bookingRef, BigDecimal amount, String currency) {
        return new Payment(bookingRef, amount, currency, "INITIATED", Instant.now());
    }

    public Long getId() { return id; }
    public String getBookingRef() { return bookingRef; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void markSuccess() { this.status = "SUCCESS"; }
    public void markFailed() { this.status = "FAILED"; }
}
