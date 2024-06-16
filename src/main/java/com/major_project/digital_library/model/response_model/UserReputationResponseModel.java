package com.major_project.digital_library.model.response_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReputationResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID userId;

    private String firstName;

    private String lastName;

    private String image;

    private String email;

    private Timestamp createdAt;

    private int totalPostAcceptances;

    private int totalReplyAcceptances;

    private int totalPostLikes;

    private int totalReplyLikes;

    private int totalScores;

    private int rank;
}
