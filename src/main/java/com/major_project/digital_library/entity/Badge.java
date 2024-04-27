package com.major_project.digital_library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Badge {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID badgeId;

    private String badgeName;

    private String description;

    private String image;

    private int value;

    private int priority;

    @ManyToOne
    @JoinColumn(name = "badgeTypeId")
    private BadgeType badgeType;

    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BadgeReward> badgeRewards = new ArrayList<>();
}
