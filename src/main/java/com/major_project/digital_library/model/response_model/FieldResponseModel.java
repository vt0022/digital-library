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
public class FieldResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID fieldId;

    private String fieldName;

    private String slug;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean isDeleted;

//    @JsonIgnore
//    private List<DocumentResponseModel> documents = new ArrayList<>();
}
