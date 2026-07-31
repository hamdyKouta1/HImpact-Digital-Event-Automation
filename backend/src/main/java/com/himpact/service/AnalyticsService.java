package com.himpact.service;

import com.himpact.entity.DashboardStatistics;
import com.himpact.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Pre-aggregated Analytics Service.
 * Serves dashboard pages strictly from pre-calculated DashboardStatistics table per PO Workstream D.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final DashboardStatisticsRepository dashboardStatisticsRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GuestRepository guestRepository;
    private final MediaFileRepository mediaFileRepository;

    private static final String SINGLETON_KEY = "SINGLETON_GLOBAL";

    @Transactional(readOnly = true)
    public DashboardStatistics getGlobalPlatformStatistics() {
        return dashboardStatisticsRepository.findById(SINGLETON_KEY)
                .orElseGet(this::recalculateGlobalStatistics);
    }

    @Transactional
    public DashboardStatistics recalculateGlobalStatistics() {
        long totalUsers = userRepository.count();
        long totalEvents = eventRepository.count();
        long totalGuests = guestRepository.count();

        DashboardStatistics stats = dashboardStatisticsRepository.findById(SINGLETON_KEY)
                .orElseGet(() -> DashboardStatistics.builder().id(SINGLETON_KEY).build());

        stats.setTotalUsers(totalUsers);
        stats.setTotalEvents(totalEvents);
        stats.setTotalGuests(totalGuests);

        log.info("Recalculated global platform statistics: Users={}, Events={}", totalUsers, totalEvents);
        return dashboardStatisticsRepository.save(stats);
    }
}
