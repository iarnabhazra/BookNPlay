package com.booknplay.booking.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByTurfIdAndSlotStart(Long turfId, Instant slotStart);
    Optional<Booking> findByTurfIdAndSlotStart(Long turfId, Instant slotStart);
    Optional<Booking> findByIdempotencyKey(String idempotencyKey);
    boolean existsByTurfIdAndSlotStartLessThanAndSlotEndGreaterThan(Long turfId, Instant end, Instant start);
}
