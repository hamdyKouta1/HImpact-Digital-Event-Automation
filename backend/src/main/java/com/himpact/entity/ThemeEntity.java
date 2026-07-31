package com.himpact.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Visual theme for event invitations and gallery.
 * Table: themes
 * See: project-index/06_Database_Design.md — themes entity
 */
@Entity
@Table(name = "themes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThemeEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "theme_name", nullable = false, length = 100)
    private String themeName;

    @Column(name = "primary_color", nullable = false, length = 7)
    @Builder.Default
    private String primaryColor = "#3B82F6";

    @Column(name = "secondary_color", nullable = false, length = 7)
    @Builder.Default
    private String secondaryColor = "#8B5CF6";

    @Column(name = "preview_image", length = 500)
    private String previewImage;

    @Column(nullable = false)
    @Builder.Default
    private boolean premium = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

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
