package com.major_project.digital_library.model.request_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionRequestModel implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;

    private String collectionName;

    private boolean isPrivate;
}
