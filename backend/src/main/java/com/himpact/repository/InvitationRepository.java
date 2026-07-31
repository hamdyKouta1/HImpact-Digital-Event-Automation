package com.himpact.repository;

import com.himpact.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    Optional<Invitation> findByGuestId(UUID guestId);
    Optional<Invitation> findByGuestInvitationCode(String invitationCode);
}
