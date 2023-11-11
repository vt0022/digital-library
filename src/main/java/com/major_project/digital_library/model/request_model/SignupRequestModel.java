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
public class SignupRequestModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String firstName;

    private String middleName;

    private String lastName;

    private Timestamp dateOfBirth;

    private int gender;

    private String phone;

    private String username;

    private String password;

    private String email;

    private UUID orgId;

    private UUID roleId;
}
