package com.booknplay.booking.service;

import com.booknplay.booking.domain.Booking;
import com.booknplay.booking.domain.BookingRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.kafka.core.KafkaTemplate;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.GlobalOpenTelemetry;

@Service
public class BookingService {

    private final BookingRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate; // may be null in tests
    private final MeterRegistry meterRegistry;
    // Executor previously used for async booking; removed for deterministic transactional behavior in tests
    // If asynchronous processing is required later, consider moving transaction boundary into the async task explicitly.
    private final Map<Long, Window> throttle = new ConcurrentHashMap<>();
    private final int peakLimitPerMinute = 30; // adjustable

    private final Tracer tracer = GlobalOpenTelemetry.getTracer("booking-service");

    public BookingService(BookingRepository repository, KafkaTemplate<String,Object> kafkaTemplate, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.meterRegistry = meterRegistry;
    }

    @Transactional
    public CompletableFuture<Booking> book(Long turfId, Instant start, Instant end, String userId, String idempotencyKey) {
        try {
            Booking booking = doBookWithRetry(turfId, start, end, userId, idempotencyKey);
            return CompletableFuture.completedFuture(booking);
        } catch (IllegalStateException dup) {
            // Wrap duplicate slot or throttle exceptions so callers can use exceptionally(...) without failing the whole test batch.
            CompletableFuture<Booking> failed = new CompletableFuture<>();
            failed.completeExceptionally(dup);
            return failed;
        }
    }

    private Booking doBookWithRetry(Long turfId, Instant start, Instant end, String userId, String idempotencyKey) {
    long startTime = System.nanoTime();
    int attempts = 0; int max = 5; long backoffMs = 20;
    while (true) {
            try {
    Span span = tracer.spanBuilder("booking.retryAttempt").startSpan();
    span.setAttribute("turf.id", turfId);
    span.setAttribute("attempt", attempts + 1);
    try {
    Booking result = doBook(turfId, start, end, userId, idempotencyKey);
        meterRegistry.timer("booking.success.timer").record(System.nanoTime() - startTime, java.util.concurrent.TimeUnit.NANOSECONDS);
        return result;
    } finally { span.end(); }
            } catch (OptimisticLockingFailureException e) {
                attempts++;
                if (attempts >= max) throw e;
        meterRegistry.counter("booking.optimistic.retry").increment();
                try { Thread.sleep(backoffMs * attempts); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }
    }

    private Booking doBook(Long turfId, Instant start, Instant end, String userId, String idempotencyKey) {
    Span span = tracer.spanBuilder("booking.create").startSpan();
    span.setAttribute("turf.id", turfId);
    span.setAttribute("user.id", userId);
    span.setAttribute("idempotency.present", idempotencyKey != null && !idempotencyKey.isBlank());
    try {
    if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existing = repository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                meterRegistry.counter("booking.idempotent.reuse").increment();
        span.setAttribute("idempotent.reused", true);
                return existing.get();
            }
        }
        if (isPeakHour(start) && !acquire(turfId)) {
            throw new IllegalStateException("Too many booking attempts - try later");
        }
        // Duplicate slot check (enforced also by DB unique constraint)
        if (repository.existsByTurfIdAndSlotStart(turfId, start)) {
            throw new IllegalStateException("Slot already booked");
        }
    Booking b = Booking.create(turfId, start, end, userId, idempotencyKey);
    Booking saved = repository.saveAndFlush(b);
    if (kafkaTemplate != null) {
        try { kafkaTemplate.send("booking-events", "booked:" + saved.getId()); } catch (Exception ignored) { }
    }
    meterRegistry.counter("booking.created.count").increment();
    return saved;
    } finally { span.end(); }
    }

    private boolean isPeakHour(Instant start) {
        int hour = start.atZone(java.time.ZoneOffset.UTC).getHour();
        return hour >= 16 && hour <= 21; // 4pm-9pm UTC
    }

    private boolean acquire(Long turfId) {
        long currentMinute = System.currentTimeMillis() / 60000;
        Window w = throttle.computeIfAbsent(turfId, k -> new Window(currentMinute));
        synchronized (w) {
            if (w.minute != currentMinute) {
                w.minute = currentMinute;
                w.counter.set(0);
            }
            if (w.counter.get() >= peakLimitPerMinute) return false;
            w.counter.incrementAndGet();
            return true;
        }
    }

    private static class Window {
        volatile long minute;
        AtomicInteger counter = new AtomicInteger();
        Window(long m) { this.minute = m; }
    }

    // Availability check (simple overlap) - cached at controller layer if cache added
    @Cacheable(value = "availability", key = "#turfId + ':' + #start.toEpochMilli() + ':' + #end.toEpochMilli()")
    public boolean isAvailable(Long turfId, Instant start, Instant end) {
        return !repository.existsByTurfIdAndSlotStartLessThanAndSlotEndGreaterThan(turfId, end, start);
    }

    @Transactional
    @CacheEvict(value = "availability", allEntries = true)
    public boolean cancel(Long bookingId, String userId) {
        return repository.findById(bookingId).map(b -> {
            if (!b.getUserId().equals(userId)) {
                throw new IllegalStateException("Not owner");
            }
            repository.delete(b);
            meterRegistry.counter("booking.cancellations").increment();
            return true;
        }).orElse(false);
    }
}
