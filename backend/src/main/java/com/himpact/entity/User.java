package com.himpact.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents an authenticated platform user.
 *
 * Users authenticate via Google OAuth2. The google_id is the stable
 * identifier received from Google — it never changes even if the user
 * changes their email address.
 *
 * Table: users
 * See: project-index/06_Database_Design.md — users entity
 * See: project-index/03_Functional_Requirements.md — FR-01 Authentication
 */
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_google_id", columnList = "google_id", unique = true),
                @Index(name = "idx_users_email", columnList = "email", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "google_id", nullable = false, unique = true, length = 128)
    private String googleId;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(name = "profile_picture", length = 500)
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.GUEST;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @Column(name = "mobile_verified")
    @Builder.Default
    private boolean mobileVerified = false;

    @Column(name = "last_login")
    private Instant lastLogin;

    // ── Audit columns (present on every business entity per PI-06) ───────────

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Version
    @Column(nullable = false)
    @Builder.Default
    private Long version = 0L;
}
