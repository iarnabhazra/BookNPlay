package com.booknplay.reporting.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class DailyReportJob {

    private static final Logger log = LoggerFactory.getLogger(DailyReportJob.class);

    private final AtomicLong lastRunMinute = new AtomicLong(-1);
    private final int maxRunsPerMinute = 1; // simple rate limit
    private final AtomicLong counter = new AtomicLong();

    @Scheduled(cron = "0 5 0 * * *") // 00:05 UTC daily
    public void generate() {
        if (!acquire()) {
            log.warn("Report generation throttled");
            return;
        }
        // Placeholder aggregation (would query booking/payment DB or via APIs)
        log.info("Generating daily report at {}", Instant.now());
        try {
            HttpClient client = HttpClient.newHttpClient();
            ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
            CompletableFuture<HttpResponse<String>> bookingsF = CompletableFuture.supplyAsync(() -> fetch(client, "http://localhost:8083/actuator/health"), exec);
            CompletableFuture<HttpResponse<String>> paymentsF = CompletableFuture.supplyAsync(() -> fetch(client, "http://localhost:8084/actuator/health"), exec);
            CompletableFuture.allOf(bookingsF, paymentsF).join();
            log.info("Report summary (health proxies) booking={} payment={}", bookingsF.get().body(), paymentsF.get().body());
        } catch (Exception e) {
            log.error("Report aggregation failed", e);
        }
    }

    private HttpResponse<String> fetch(HttpClient client, String url) {
        try {
            return client.send(HttpRequest.newBuilder(URI.create(url)).GET().build(), HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean acquire() {
        long minute = System.currentTimeMillis() / 60000;
        long lr = lastRunMinute.get();
        if (lr != minute) {
            lastRunMinute.set(minute);
            counter.set(0);
        }
        return counter.incrementAndGet() <= maxRunsPerMinute;
    }
}
