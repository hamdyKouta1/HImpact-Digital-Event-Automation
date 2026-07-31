package com.himpact.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for tracking failed login attempts and enforcing account lockout.
 * Locks IP/key after 5 consecutive failed attempts for 15 minutes per Workstream B.
 */
@Slf4j
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MS = 15 * 60 * 1000L; // 15 minutes

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Long> lockTimeCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
        lockTimeCache.remove(key);
    }

    public void loginFailed(String key) {
        int attempts = attemptsCache.getOrDefault(key, 0) + 1;
        attemptsCache.put(key, attempts);
        if (attempts >= MAX_ATTEMPTS) {
            lockTimeCache.put(key, System.currentTimeMillis() + LOCK_TIME_MS);
            log.warn("Account/IP [{}] locked out due to {} failed login attempts.", key, attempts);
        }
    }

    public boolean isBlocked(String key) {
        Long lockTime = lockTimeCache.get(key);
        if (lockTime != null) {
            if (System.currentTimeMillis() > lockTime) {
                // Lock expired
                lockTimeCache.remove(key);
                attemptsCache.remove(key);
                return false;
            }
            return true;
        }
        return false;
    }
}
