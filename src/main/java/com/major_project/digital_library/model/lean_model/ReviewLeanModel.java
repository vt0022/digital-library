package com.major_project.digital_library.model.lean_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLeanModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID reviewId;

    private int star;

    private String content;
}
