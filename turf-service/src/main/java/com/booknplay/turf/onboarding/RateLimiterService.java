package com.booknplay.turf.onboarding;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimiterService {
    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final int limitPerMinute = 5; // configurable

    public boolean tryAcquire(String key) {
        long currentMinute = Instant.now().getEpochSecond() / 60;
        Window w = windows.computeIfAbsent(key, k -> new Window(currentMinute));
        synchronized (w) {
            if (w.minute != currentMinute) {
                w.minute = currentMinute;
                w.counter.set(0);
            }
            if (w.counter.get() >= limitPerMinute) return false;
            w.counter.incrementAndGet();
            return true;
        }
    }

    private static class Window {
        volatile long minute;
        AtomicInteger counter = new AtomicInteger();
        Window(long m) { this.minute = m; }
    }
}
