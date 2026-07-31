package com.himpact.repository;

import com.himpact.entity.Event;
import com.himpact.entity.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data access layer for the Event entity.
 * See: project-index/05_Software_Architecture.md — Data Layer
 */
@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByOwnerIdAndIsDeletedFalse(UUID ownerId);

    Page<Event> findByOwnerIdAndIsDeletedFalse(UUID ownerId, Pageable pageable);

    Optional<Event> findBySlugAndIsDeletedFalse(String slug);

    Optional<Event> findByIdAndIsDeletedFalse(UUID id);

    boolean existsBySlug(String slug);

    long countByStatus(EventStatus status);
}
