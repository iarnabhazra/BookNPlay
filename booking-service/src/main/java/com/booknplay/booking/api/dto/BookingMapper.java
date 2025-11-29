package com.booknplay.booking.api.dto;

import com.booknplay.booking.domain.Booking;

public final class BookingMapper {
    private BookingMapper() {}
    public static BookingDto toDto(Booking b) {
        return new BookingDto(b.getId(), b.getTurfId(), b.getSlotStart(), b.getSlotEnd(), b.getUserId(), b.getIdempotencyKey());
    }
}
