package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Badge;
import com.major_project.digital_library.entity.BadgeReward;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.entity.UserBadgeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBadgeRewardRepository extends JpaRepository<BadgeReward, UserBadgeKey> {
    boolean existsByUserAndBadge(User user, Badge badge);

    @Query("SELECT br FROM BadgeReward br JOIN Badge b " +
            "ON b.badgeId = br.badge.badgeId " +
            "WHERE br.user = :user " +
            "ORDER BY br.rewardedAt ASC")
    List<BadgeReward> findByUser(User user);
}
