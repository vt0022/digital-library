package com.major_project.digital_library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Builder
public class AuthModel {
    private String accessToken;
    private String refreshToken;
}
