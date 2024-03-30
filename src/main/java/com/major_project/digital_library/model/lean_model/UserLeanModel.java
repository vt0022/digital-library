package com.major_project.digital_library.model.lean_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserLeanModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID userId;

    private String firstName;

    private String lastName;

    private String email;
    
    private String image;
}
