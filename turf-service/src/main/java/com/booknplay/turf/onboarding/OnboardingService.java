package com.booknplay.turf.onboarding;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class OnboardingService {

    private final TurfOnboardingRepository repository;
    private final RateLimiterService rateLimiterService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OnboardingService(TurfOnboardingRepository repository, RateLimiterService rateLimiterService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.rateLimiterService = rateLimiterService;
        this.kafkaTemplate = kafkaTemplate;
    }

    private final ExecutorService batchExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @Transactional
    public TurfOnboardingRequest submit(String ownerEmail, String turfName, String location) {
        if (!rateLimiterService.tryAcquire(ownerEmail)) {
            throw new IllegalStateException("Rate limit exceeded");
        }
    TurfOnboardingRequest req = TurfOnboardingRequest.create(ownerEmail, turfName, location, "PENDING", Instant.now());
        repository.save(req);
        return req;
    }

    @Scheduled(fixedDelay = 5000)
    public void processBatch() {
        List<TurfOnboardingRequest> batch = repository.findTop50ByStatusOrderByCreatedAtAsc("PENDING");
        if (batch.isEmpty()) return;
        batchExecutor.submit(() -> batch.parallelStream().forEach(req -> {
            req.setStatus("APPROVED");
            repository.save(req);
            kafkaTemplate.send("onboarding-events", "approved:" + req.getId());
        }));
    }
}
