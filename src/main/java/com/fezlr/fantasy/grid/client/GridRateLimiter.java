package com.fezlr.fantasy.grid.client;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class GridRateLimiter {
    private static final Duration MIN_INTERVAL = Duration.ofMillis(3100);
    private final Object lock = new Object();
    private Instant nextAllowedAt = Instant.now();

    public void acquire() {
        synchronized (lock) {
            Instant now = Instant.now();
            if (now.isBefore(nextAllowedAt)) {
                long waitMs = Duration.between(now, nextAllowedAt).toMillis();
                try {
                    Thread.sleep(waitMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            nextAllowedAt = Instant.now().plus(MIN_INTERVAL);
        }
    }
}