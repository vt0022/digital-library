package com.major_project.digital_library.model.request_model;

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
public class UserRequestModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String firstName;

    private String lastName;

    private Timestamp dateOfBirth;

    private int gender;

    private String phone;

    private String email;

    private String password;

    private String confirmPassword;

    private UUID orgId;

    private UUID roleId;
}
