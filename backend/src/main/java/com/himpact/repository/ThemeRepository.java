package com.himpact.repository;

import com.himpact.entity.ThemeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThemeRepository extends JpaRepository<ThemeEntity, UUID> {
    List<ThemeEntity> findByActiveTrue();
}
