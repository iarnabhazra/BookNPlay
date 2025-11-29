package com.booknplay.turf.onboarding;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "turf_onboarding_request")
public class TurfOnboardingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ownerEmail;
    private String turfName;
    private String location;
    private String status; // PENDING, APPROVED, REJECTED
    private Instant createdAt;

    public TurfOnboardingRequest() {}

    public TurfOnboardingRequest(Long id, String ownerEmail, String turfName, String location, String status, Instant createdAt) {
        this.id = id;
        this.ownerEmail = ownerEmail;
        this.turfName = turfName;
        this.location = location;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static TurfOnboardingRequest create(String ownerEmail, String turfName, String location, String status, Instant createdAt) {
        return new TurfOnboardingRequest(null, ownerEmail, turfName, location, status, createdAt);
    }

    public Long getId() { return id; }
    public String getOwnerEmail() { return ownerEmail; }
    public String getTurfName() { return turfName; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    public void setTurfName(String turfName) { this.turfName = turfName; }
    public void setLocation(String location) { this.location = location; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
