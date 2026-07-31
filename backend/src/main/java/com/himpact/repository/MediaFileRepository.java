package com.himpact.repository;

import com.himpact.entity.MediaFile;
import com.himpact.entity.UploadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, UUID> {

    Page<MediaFile> findByEventIdAndUploadStatusAndIsDeletedFalse(
            UUID eventId, UploadStatus uploadStatus, Pageable pageable);

    Optional<MediaFile> findByIdAndIsDeletedFalse(UUID id);

    long countByEventIdAndUploadStatusAndIsDeletedFalse(UUID eventId, UploadStatus status);

    @Query("SELECT COALESCE(SUM(m.fileSize), 0) FROM MediaFile m WHERE m.event.id = :eventId AND m.uploadStatus = 'COMPLETED' AND m.isDeleted = false")
    long calculateTotalStorageBytesForEvent(@Param("eventId") UUID eventId);
}
