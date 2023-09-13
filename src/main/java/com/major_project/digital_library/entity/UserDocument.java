package com.major_project.digital_library.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID userId;

    private UUID docId;
}
