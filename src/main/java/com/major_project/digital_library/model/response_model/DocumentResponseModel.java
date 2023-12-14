package com.major_project.digital_library.model.response_model;

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
public class DocumentResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID docId;

    private String docName;

    private String docIntroduction;

    private String viewUrl;

    private String downloadUrl;

    private String slug;

    private Timestamp uploadedAt;

    private Timestamp updatedAt;

    private boolean isDeleted;

    private int verifiedStatus;

    private String note;

    private int totalView;

    private int totalFavorite;

    private double averageRating;

    private boolean isInternal;

    private String author;

    private String thumbnail;
}
