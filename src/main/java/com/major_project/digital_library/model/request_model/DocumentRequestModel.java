package com.major_project.digital_library.model.request_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequestModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String docName;

    private String docIntroduction;

    private int verifiedStatus;

    private boolean isInternal;

    private UUID orgId;

    private UUID categoryId;

    private UUID fieldId;

}
