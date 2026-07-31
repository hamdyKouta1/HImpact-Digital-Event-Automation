package com.himpact.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Guest JPA entity — represents an invited participant in an event.
 *
 * Table: guests
 * See: project-index/06_Database_Design.md — guests entity
 * See: project-index/03_Functional_Requirements.md — FR-04 Guest Management
 */
@Entity
@Table(
        name = "guests",
        indexes = {
                @Index(name = "idx_guests_event_id", columnList = "event_id"),
                @Index(name = "idx_guests_invitation_code", columnList = "invitation_code", unique = true),
                @Index(name = "idx_guests_mobile", columnList = "mobile"),
                @Index(name = "idx_guests_event_status", columnList = "event_id, status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guest {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(length = 20)
    private String mobile;

    @Column(length = 255)
    private String email;

    @Column(name = "invitation_code", nullable = false, unique = true, length = 64)
    private String invitationCode;

    @Column(name = "invitation_url", length = 500)
    private String invitationUrl;

    @Column(name = "upload_limit", nullable = false)
    @Builder.Default
    private Integer uploadLimit = 30;

    @Column(name = "uploaded_count", nullable = false)
    @Builder.Default
    private Integer uploadedCount = 0;

    @Column(name = "storage_used_mb", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal storageUsedMb = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private GuestStatus status = GuestStatus.INVITED;

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
