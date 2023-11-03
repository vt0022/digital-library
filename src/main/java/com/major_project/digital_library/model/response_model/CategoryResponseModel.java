package com.major_project.digital_library.model.response_model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID categoryId;

    private String categoryName;

    private String slug;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean isDeleted;

    @JsonIgnore
    private List<DocumentResponseModel> documents = new ArrayList<>();
}
