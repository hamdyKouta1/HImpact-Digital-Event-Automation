package com.himpact.repository;

import com.himpact.entity.AttendanceStatus;
import com.himpact.entity.Rsvp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RsvpRepository extends JpaRepository<Rsvp, UUID> {
    Optional<Rsvp> findByGuestId(UUID guestId);
    List<Rsvp> findByEventIdAndIsDeletedFalse(UUID eventId);
    long countByEventIdAndAttendanceStatusAndIsDeletedFalse(UUID eventId, AttendanceStatus status);
}
