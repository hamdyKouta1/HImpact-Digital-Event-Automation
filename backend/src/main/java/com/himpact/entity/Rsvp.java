package com.himpact.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * RSVP JPA entity — tracks guest RSVP attendance responses.
 * Table: rsvp
 * See: project-index/06_Database_Design.md — rsvp entity
 * See: project-index/03_Functional_Requirements.md — FR-05 RSVP
 */
@Entity
@Table(
        name = "rsvp",
        indexes = {
                @Index(name = "idx_rsvp_event_id", columnList = "event_id"),
                @Index(name = "idx_rsvp_guest_id", columnList = "guest_id", unique = true),
                @Index(name = "idx_rsvp_status", columnList = "event_id, attendance_status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rsvp {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_id", nullable = false, unique = true)
    private Guest guest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", nullable = false, length = 20)
    @Builder.Default
    private AttendanceStatus attendanceStatus = AttendanceStatus.PENDING;

    @Column(name = "attendee_count", nullable = false)
    @Builder.Default
    private Integer attendeeCount = 1;

    @Column(name = "response_time")
    private Instant responseTime;

    @Column(columnDefinition = "TEXT")
    private String notes;

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
