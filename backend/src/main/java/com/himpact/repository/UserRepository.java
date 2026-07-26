package com.himpact.repository;

import com.himpact.entity.User;
import com.himpact.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Data access layer for the User entity.
 * Business logic must never be placed here — only database queries.
 *
 * See: project-index/05_Software_Architecture.md — Data Layer
 * See: project-index/06_Database_Design.md — users entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByGoogleId(String googleId);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    boolean existsByGoogleId(String googleId);

    boolean existsByEmail(String email);

    long countByRole(UserRole role);
}
