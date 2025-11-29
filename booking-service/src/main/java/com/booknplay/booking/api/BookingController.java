package com.booknplay.booking.api;

import com.booknplay.booking.domain.Booking;
import com.booknplay.booking.api.dto.BookingDto;
import com.booknplay.booking.api.dto.BookingMapper;
import com.booknplay.booking.service.BookingService;
import org.springframework.http.ResponseEntity;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @RateLimiter(name = "bookingApi", fallbackMethod = "bookingRateLimited")
    @Bulkhead(name = "bookingApi")
    @CircuitBreaker(name = "bookingApi", fallbackMethod = "bookingCircuitOpen")
    public ResponseEntity<?> create(@RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey,
                    @RequestHeader(value = "X-User-Id", required = false) String userHeader,
                    @Valid @RequestBody BookingRequest request) throws ExecutionException, InterruptedException {
    String userId = userHeader != null ? userHeader : request.userId; // fallback to body for now
    if (request.slotStart.isAfter(request.slotEnd) || request.slotStart.equals(request.slotEnd)) {
        return ResponseEntity.badRequest().body("slotStart must be before slotEnd");
    }
    long durationMinutes = java.time.Duration.between(request.slotStart, request.slotEnd).toMinutes();
    if (durationMinutes <= 0 || durationMinutes > 240) {
        return ResponseEntity.badRequest().body("Booking duration invalid (must be 1-240 minutes)");
    }
    Booking b = bookingService.book(request.turfId, request.slotStart, request.slotEnd, userId, idempotencyKey).get();
    boolean idempotent = idempotencyKey != null && !idempotencyKey.isBlank();
    BookingDto dto = BookingMapper.toDto(b);
    return ResponseEntity.ok(Map.of(
        "booking", dto,
        "idempotent", idempotent,
        "idempotencyKey", idempotencyKey
    ));
    }

    @GetMapping("/availability")
    public ResponseEntity<?> availability(@RequestParam Long turfId, @RequestParam Instant start, @RequestParam Instant end) {
        boolean free = bookingService.isAvailable(turfId, start, end);
        return ResponseEntity.ok(Map.of("available", free));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id, @RequestParam String userId) {
        boolean ok = bookingService.cancel(id, userId);
        return ok ? ResponseEntity.ok(Map.of("cancelled", true)) : ResponseEntity.notFound().build();
    }

    // Fallback must mirror method signature + Throwable at end
    private ResponseEntity<?> bookingRateLimited(String idempotencyKey, BookingRequest request, Throwable t) {
        return ResponseEntity.status(429).body("Booking rate limit exceeded. Please retry.");
    }

    // Overloaded fallback to match actual controller method signature for RateLimiter
    private ResponseEntity<?> bookingRateLimited(String idempotencyKey, String userHeader, BookingRequest request, Throwable t) {
        return ResponseEntity.status(429).body("Booking rate limit exceeded. Please retry.");
    }

    // Circuit breaker fallback (same signature + Throwable)
    private ResponseEntity<?> bookingCircuitOpen(String idempotencyKey, BookingRequest request, Throwable t) {
        return ResponseEntity.status(503).body("Booking service temporarily unavailable.");
    }

    // Overloaded fallback to match actual controller method signature
    private ResponseEntity<?> bookingCircuitOpen(String idempotencyKey, String userHeader, BookingRequest request, Throwable t) {
        return ResponseEntity.status(503).body("Booking service temporarily unavailable.");
    }

    static class BookingRequest {
    @NotNull public Long turfId;
    @NotNull public Instant slotStart;
    @NotNull public Instant slotEnd;
    @NotNull public String userId;
    }
}
