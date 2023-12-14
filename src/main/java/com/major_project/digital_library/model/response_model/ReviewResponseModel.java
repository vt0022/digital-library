package com.major_project.digital_library.model.response_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID reviewId;

    private int star;

    private String content;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private UserResponseModel user;

    private DocumentResponseModel document;
}
