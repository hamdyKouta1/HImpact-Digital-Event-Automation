package com.himpact.repository;

import com.himpact.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PackageRepository extends JpaRepository<PackageEntity, UUID> {
    List<PackageEntity> findByActiveTrueOrderByDisplayOrderAsc();
}
