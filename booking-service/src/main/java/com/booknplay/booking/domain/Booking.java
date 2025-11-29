package com.booknplay.booking.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "booking", uniqueConstraints = @UniqueConstraint(columnNames = {"turfId", "slotStart"}))
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long turfId;
    private Instant slotStart;
    private Instant slotEnd;
    private String userId;
    // Optional idempotency key supplied by client to prevent duplicate bookings
    @Column(unique = true, length = 64)
    private String idempotencyKey;
    @Version
    private long version;

    public Booking() {}

    public Booking(Long id, Long turfId, Instant slotStart, Instant slotEnd, String userId, String idempotencyKey, long version) {
        this.id = id;
        this.turfId = turfId;
        this.slotStart = slotStart;
        this.slotEnd = slotEnd;
        this.userId = userId;
        this.idempotencyKey = idempotencyKey;
        this.version = version;
    }

    public static Booking create(Long turfId, Instant slotStart, Instant slotEnd, String userId, String idempotencyKey) {
        return new Booking(null, turfId, slotStart, slotEnd, userId, idempotencyKey, 0L);
    }

    public Long getId() { return id; }
    public Long getTurfId() { return turfId; }
    public Instant getSlotStart() { return slotStart; }
    public Instant getSlotEnd() { return slotEnd; }
    public String getUserId() { return userId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public long getVersion() { return version; }

    public void setId(Long id) { this.id = id; }
    public void setTurfId(Long turfId) { this.turfId = turfId; }
    public void setSlotStart(Instant slotStart) { this.slotStart = slotStart; }
    public void setSlotEnd(Instant slotEnd) { this.slotEnd = slotEnd; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public void setVersion(long version) { this.version = version; }
}
