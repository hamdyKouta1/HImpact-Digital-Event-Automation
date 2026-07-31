package com.himpact.repository;

import com.himpact.entity.MediaSync;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaSyncRepository extends JpaRepository<MediaSync, UUID> {
    Optional<MediaSync> findByGuestIdAndLocalIdentifier(UUID guestId, String localIdentifier);
    boolean existsByGuestIdAndLocalIdentifier(UUID guestId, String localIdentifier);
}
