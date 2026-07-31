package com.himpact.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Pre-aggregated platform statistics for instant Admin & Owner dashboard loads.
 * Eliminates expensive runtime table scans per PO Workstream D Requirement.
 *
 * Table: dashboard_statistics
 */
@Entity
@Table(name = "dashboard_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatistics {

    @Id
    @Column(nullable = false, length = 50)
    private String id; // SINGLETON_GLOBAL, or EVENT_{eventId}

    @Column(name = "total_users", nullable = false)
    @Builder.Default
    private Long totalUsers = 0L;

    @Column(name = "total_events", nullable = false)
    @Builder.Default
    private Long totalEvents = 0L;

    @Column(name = "published_events", nullable = false)
    @Builder.Default
    private Long publishedEvents = 0L;

    @Column(name = "total_guests", nullable = false)
    @Builder.Default
    private Long totalGuests = 0L;

    @Column(name = "total_invitation_views", nullable = false)
    @Builder.Default
    private Long totalInvitationViews = 0L;

    @Column(name = "total_rsvps", nullable = false)
    @Builder.Default
    private Long totalRsvps = 0L;

    @Column(name = "total_uploads", nullable = false)
    @Builder.Default
    private Long totalUploads = 0L;

    @Column(name = "total_storage_bytes", nullable = false)
    @Builder.Default
    private Long totalStorageBytes = 0L;

    @Column(name = "total_revenue", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
