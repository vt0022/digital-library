package com.major_project.digital_library.model.response_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentNoteResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int page;

    private String content;

    private Timestamp notedAt;
}
