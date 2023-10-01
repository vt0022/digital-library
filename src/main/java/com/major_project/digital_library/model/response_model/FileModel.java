package com.major_project.digital_library.model.response_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String docName;

    private String viewUrl;

    private String downloadUrl;

    private String thumbnail;
}
