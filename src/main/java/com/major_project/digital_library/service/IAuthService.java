package com.major_project.digital_library.service;

import com.major_project.digital_library.model.AuthModel;
import com.major_project.digital_library.model.request_model.LoginRequestModel;
import com.major_project.digital_library.model.request_model.SignupRequestModel;
import com.major_project.digital_library.model.response_model.UserResponseModel;

import java.util.Map;

public interface IAuthService {
    UserResponseModel signup(SignupRequestModel signupRequest);

    AuthModel login(LoginRequestModel loginRequestModel);

    void sendEmail(String email, String type);

    AuthModel verifyUser(String email, int code, String type);

    Map<String, String> refresh(Map<String, String> refreshToken);

    AuthModel loginWithGoogle(Map<String, String> loginRequest);

    AuthModel signupWithGoogle(Map<String, String> signupRequest);
}
