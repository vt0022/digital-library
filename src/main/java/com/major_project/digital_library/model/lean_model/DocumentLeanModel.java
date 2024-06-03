package com.major_project.digital_library.model.lean_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentLeanModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID docId;

    private String docName;

    private String docIntroduction;

    private String viewUrl;

    private String downloadUrl;

    private String slug;

    private String note;
}
