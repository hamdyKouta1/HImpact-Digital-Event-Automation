package com.himpact.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lightweight Rate Limiting Filter restricting high-frequency requests per IP.
 * Prevents DoS attacks and API abuse per Sprint 6 Workstream B.
 */
@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 120;
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> windowStartTimes = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String clientIp = getClientIp(request);
        long now = System.currentTimeMillis();

        windowStartTimes.putIfAbsent(clientIp, now);
        requestCounts.putIfAbsent(clientIp, new AtomicInteger(0));

        if (now - windowStartTimes.get(clientIp) > 60000) {
            // Reset window
            windowStartTimes.put(clientIp, now);
            requestCounts.get(clientIp).set(0);

            // Evict stale IP entries if cache size exceeds safety limit (ISSUE-H02)
            if (requestCounts.size() > 5000) {
                requestCounts.entrySet().removeIf(e -> !windowStartTimes.containsKey(e.getKey()) || (now - windowStartTimes.get(e.getKey()) > 3600000));
                windowStartTimes.entrySet().removeIf(e -> (now - e.getValue() > 3600000));
            }
        }

        int count = requestCounts.get(clientIp).incrementAndGet();

        if (count > MAX_REQUESTS_PER_MINUTE) {
            log.warn("Rate limit exceeded for IP [{}] on path [{}]", clientIp, request.getRequestURI());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Too many requests. Please try again later.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        return xf != null ? xf.split(",")[0] : request.getRemoteAddr();
    }
}
