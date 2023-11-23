package com.major_project.digital_library.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;

    private String roleName;
}
