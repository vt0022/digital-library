package com.major_project.digital_library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectionLikeModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID collectionId;

    private Timestamp likedAt;
}
