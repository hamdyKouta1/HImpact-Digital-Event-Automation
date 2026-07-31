package com.himpact.repository;

import com.himpact.entity.DashboardStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardStatisticsRepository extends JpaRepository<DashboardStatistics, String> {
}
