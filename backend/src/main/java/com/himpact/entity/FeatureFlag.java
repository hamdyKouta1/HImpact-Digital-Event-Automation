package com.himpact.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * FeatureFlag JPA entity — database & environment configurable feature toggles.
 * Flags: GOOGLE_DRIVE, PAYMENTS, MEDIA, COMMENTS, RSVP, NOTIFICATIONS
 *
 * See: PO Sprint 5 Feature Flags Architecture Requirement
 */
@Entity
@Table(name = "feature_flags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureFlag {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "flag_name", nullable = false, unique = true, length = 100)
    private String flagName;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(length = 255)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
