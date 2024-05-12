package com.major_project.digital_library.model.response_model;

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
@AllArgsConstructor
@NoArgsConstructor
public class DetailSectionResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID sectionId;

    private String sectionName;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private List<DetailSubsectionResponseModel> subsections = new ArrayList<>();
}
