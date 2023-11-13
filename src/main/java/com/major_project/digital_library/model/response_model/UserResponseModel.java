package com.major_project.digital_library.model.response_model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.Review;
import com.major_project.digital_library.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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

    private String phone;

    private String image;

    @JsonIgnore
    private String password;

    private String email;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean isDeleted;

    @JsonIgnore
    private Organization organization;

    @JsonIgnore
    private Role role;

    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();
}
