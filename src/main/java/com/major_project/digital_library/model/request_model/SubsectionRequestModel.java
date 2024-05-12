package com.major_project.digital_library.model.request_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubsectionRequestModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String subName;

    private boolean isEditable;

    private UUID sectionId;
}
