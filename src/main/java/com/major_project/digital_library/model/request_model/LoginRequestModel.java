package com.major_project.digital_library.model.request_model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Builder
public class LoginRequestModel {
    private String email;
    private String password;
}
