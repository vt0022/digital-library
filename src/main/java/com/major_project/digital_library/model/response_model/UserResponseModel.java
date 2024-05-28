package com.major_project.digital_library.model.response_model;

import com.major_project.digital_library.model.RoleModel;
import com.major_project.digital_library.model.lean_model.BadgeLeanModel;
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
public class UserResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID userId;

    private String firstName;

    private String lastName;

    private Timestamp dateOfBirth;

    private int gender;

    private String image;

    private String email;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean isDisabled;

    private boolean isAuthenticated;

    private int totalLikes;

    private int totalViews;

    private int totalDocuments;

    private int totalPostLikes;

    private int totalPosts;

    private int totalReplies;

    private RoleModel role;

    private OrganizationResponseModel organization;

    private BadgeLeanModel badge;
}
