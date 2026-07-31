package com.himpact.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * MediaFile JPA entity — represents an uploaded photo or video.
 * Table: media_files
 * See: project-index/06_Database_Design.md — media_files entity
 */
@Entity
@Table(
        name = "media_files",
        indexes = {
                @Index(name = "idx_media_event_id", columnList = "event_id"),
                @Index(name = "idx_media_guest_id", columnList = "guest_id"),
                @Index(name = "idx_media_upload_status", columnList = "upload_status"),
                @Index(name = "idx_media_event_guest", columnList = "event_id, guest_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaFile {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "storage_filename", nullable = false, length = 255)
    private String storageFilename;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "image_width")
    private Integer imageWidth;

    @Column(name = "image_height")
    private Integer imageHeight;

    @Column(name = "storage_provider", nullable = false, length = 50)
    @Builder.Default
    private String storageProvider = "GOOGLE_DRIVE";

    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false, length = 30)
    @Builder.Default
    private UploadStatus uploadStatus = UploadStatus.PENDING;

    @Column(name = "uploaded_at")
    private Instant uploadedAt;

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
