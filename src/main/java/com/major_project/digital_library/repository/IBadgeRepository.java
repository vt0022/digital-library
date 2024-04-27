package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Badge;
import com.major_project.digital_library.entity.BadgeType;
import com.major_project.digital_library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IBadgeRepository extends JpaRepository<Badge, UUID> {
    Optional<Badge> findByBadgeTypeAndValue(BadgeType badgeType, int value);

    List<Badge> findByBadgeTypeAndValueLessThanEqual(BadgeType badgeType, int value);

    @Query("SELECT b FROM Badge b JOIN BadgeReward br " +
            "ON b.badgeId = br.badge.badgeId " +
            "WHERE br.user = :user " +
            "ORDER BY b.priority DESC LIMIT 1")
    Optional<Badge> findBestBadgeByUser(User user);
}
