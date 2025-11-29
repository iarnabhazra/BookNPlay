package com.booknplay.turf.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface TurfOnboardingRepository extends JpaRepository<TurfOnboardingRequest, Long> {
    List<TurfOnboardingRequest> findTop50ByStatusOrderByCreatedAtAsc(String status);
    long countByOwnerEmailAndCreatedAtAfter(String ownerEmail, Instant after);
}
