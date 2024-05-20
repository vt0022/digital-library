package com.major_project.digital_library.model.response_model;

import com.major_project.digital_library.model.lean_model.UserLeanModel;
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

    private int verifiedStatus;

    private String note;

    private int timesLeft;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private Timestamp verifiedAt;

    private UserResponseModel user;

    private UserLeanModel userVerified;

    private DocumentResponseModel document;
}
