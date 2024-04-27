package com.major_project.digital_library.model.response_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BadgeResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID badgeId;

    private String badgeName;

    private String description;

    private String image;

    private Timestamp rewardedAt;
}
