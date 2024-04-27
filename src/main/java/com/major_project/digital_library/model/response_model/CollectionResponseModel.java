package com.major_project.digital_library.model.response_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionResponseModel implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;

    private UUID collectionId;

    private String collectionName;

    private String slug;

    private boolean isPrivate;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private List<String> thumbnails;
}
