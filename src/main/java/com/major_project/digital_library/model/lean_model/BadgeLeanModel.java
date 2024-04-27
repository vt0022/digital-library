package com.major_project.digital_library.model.lean_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BadgeLeanModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID badgeId;

    private String badgeName;

    private String image;
}
