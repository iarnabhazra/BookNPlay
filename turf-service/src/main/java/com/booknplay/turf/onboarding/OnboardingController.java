package com.booknplay.turf.onboarding;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/turfs/onboarding")
@SuppressWarnings("unused")
public class OnboardingController {

    private final OnboardingService service;

    public OnboardingController(OnboardingService service) {
        this.service = service;
    }

    @PostMapping
    @RateLimiter(name = "onboardingApi", fallbackMethod = "onboardingRateLimited")
    public ResponseEntity<?> submit(@RequestBody SubmitRequest request) {
        return ResponseEntity.ok(service.submit(request.ownerEmail, request.turfName, request.location));
    }

    // Fallback method signature must match original + Throwable
    private ResponseEntity<?> onboardingRateLimited(SubmitRequest request, Throwable t) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Onboarding rate limit exceeded. Please retry later.");
    }

    static class SubmitRequest {
    @Email public String ownerEmail;
    @NotBlank public String turfName;
    @NotBlank public String location;
    }
}
