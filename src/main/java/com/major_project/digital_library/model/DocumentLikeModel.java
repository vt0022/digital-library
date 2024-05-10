package com.major_project.digital_library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentLikeModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String slug;

    private Timestamp likedAt;
}
