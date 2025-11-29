package com.booknplay.booking.api.dto;

import java.time.Instant;

public record BookingDto(Long id, Long turfId, Instant slotStart, Instant slotEnd, String userId, String idempotencyKey) {}
