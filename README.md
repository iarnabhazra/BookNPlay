# BooknPlay Platform

Modular microservices architecture for turf booking & management demonstrating advanced concurrency, reactive programming, rate limiting, batch processing, messaging, caching, and reporting.

## Modules (initial)
- gateway (Spring Cloud Gateway + WebFlux)
- auth-service (RBAC, user management, JWT)
- turf-service (turf onboarding, rate limiter, batch + messaging)
- booking-service (concurrent booking engine, search, throttling)
- payment-service (parallel processing, dynamic pricing, settlements)
- notification-service (async MQ, email, WebSocket push)
- reporting-service (EOD batch reports, throttled)
- commons (shared DTOs, events, utils)

## Tech Stack
Java 21 (Virtual Threads), Spring Boot 3.x, Spring WebFlux, Spring Security, Spring Data JPA, Spring Scheduler, Redis/Caffeine, Kafka/RabbitMQ (abstracted), Micrometer, Resilience4j, Docker.

## Quick Start (to be completed as modules implemented)
Prereq: Java 21 installed.

Build:
```
./mvnw -DskipTests install
```

Infrastructure (databases, Kafka, Redis, MailHog):
```
docker compose up -d
```

Access Mail UI: http://localhost:8025

Kafka bootstrap: localhost:9092

## Concurrency & Performance Highlights
- Virtual thread executors for high-throughput request handling
- Structured concurrency for grouped tasks
- Rate limiting via Redis + token bucket / resilience4j
- Batch processing via Scheduler + chunked executors
- Optimistic locking for bookings
- Parallel search using custom ForkJoin + virtual threads

## Next Steps
1. Add parent POM with dependency management
2. Scaffold module POMs
3. Implement commons library + event model
4. Stand up auth-service with RBAC & JWT
5. Add gateway routing & reactive filters
 6. Add automated tests & infrastructure (IN PROGRESS)

---
This repo is instructional and production-inspired.
"# bookNplay" 
"# BookNPlay" 
