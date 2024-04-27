package com.major_project.digital_library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@IdClass(UserBadgeKey.class)
public class BadgeReward implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "badgeId")
    private Badge badge;

    private Timestamp rewardedAt;

    @PrePersist
    public void onCreate() {
        rewardedAt = new Timestamp(System.currentTimeMillis());
    }
}
