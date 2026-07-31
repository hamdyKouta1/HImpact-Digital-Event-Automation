package com.himpact.repository;

import com.himpact.entity.Guest;
import com.himpact.entity.GuestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Data access layer for Guest entity.
 * See: project-index/05_Software_Architecture.md — Data Layer
 */
@Repository
public interface GuestRepository extends JpaRepository<Guest, UUID> {

    Page<Guest> findByEventIdAndIsDeletedFalse(UUID eventId, Pageable pageable);

    Optional<Guest> findByInvitationCodeAndIsDeletedFalse(String invitationCode);

    Optional<Guest> findByEventIdAndEmailAndIsDeletedFalse(UUID eventId, String email);

    Optional<Guest> findByEventIdAndMobileAndIsDeletedFalse(UUID eventId, String mobile);

    boolean existsByInvitationCode(String invitationCode);

    long countByEventIdAndIsDeletedFalse(UUID eventId);

    long countByEventIdAndStatusAndIsDeletedFalse(UUID eventId, GuestStatus status);

    @Query("""
        SELECT g FROM Guest g
        WHERE g.event.id = :eventId
          AND g.isDeleted = false
          AND (:search IS NULL OR LOWER(g.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR g.mobile LIKE CONCAT('%', :search, '%')
               OR LOWER(g.email) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:status IS NULL OR g.status = :status)
    """)
    Page<Guest> searchGuests(
            @Param("eventId") UUID eventId,
            @Param("search") String search,
            @Param("status") GuestStatus status,
            Pageable pageable
    );
}
