package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IBadgeTypeRepository extends JpaRepository<BadgeType, UUID> {
    Optional<BadgeType> findByUnit(String unit);
}
