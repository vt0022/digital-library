package com.major_project.digital_library.model.response_model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Builder
public class ResponseModel<T> {
    private int status;
    private boolean error;
    private String message;
    private T data;
}
