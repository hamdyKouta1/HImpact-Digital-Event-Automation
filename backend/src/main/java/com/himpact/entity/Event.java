package com.himpact.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;
import java.util.UUID;

/**
 * Event JPA entity — core business entity representing a single event.
 * Architecture is event-agnostic (supports Wedding, Birthday, Corporate etc. per DEC-003).
 *
 * Table: events
 * See: project-index/06_Database_Design.md — events entity
 * See: project-index/03_Functional_Requirements.md — FR-02 Event Management
 */
@Entity
@Table(
        name = "events",
        indexes = {
                @Index(name = "idx_events_owner_id", columnList = "owner_id"),
                @Index(name = "idx_events_status", columnList = "status"),
                @Index(name = "idx_events_event_date", columnList = "event_date"),
                @Index(name = "idx_events_slug", columnList = "slug", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    @Builder.Default
    private EventType eventType = EventType.WEDDING;

    @Column(name = "bride_name", length = 100)
    private String brideName;

    @Column(name = "groom_name", length = 100)
    private String groomName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "venue_name", length = 255)
    private String venueName;

    @Column(name = "venue_address", columnDefinition = "TEXT")
    private String venueAddress;

    @Column(name = "google_maps_url", length = 500)
    private String googleMapsUrl;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "cover_image", length = 500)
    private String coverImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private PackageEntity packageEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private ThemeEntity theme;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EventStatus status = EventStatus.DRAFT;

    @Column(length = 100, unique = true)
    private String slug;

    // ── Audit columns ────────────────────────────────────────────────────────

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Version
    @Column(nullable = false)
    @Builder.Default
    private Long version = 0L;
}
