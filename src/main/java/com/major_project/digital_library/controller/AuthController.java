package com.major_project.digital_library.controller;

import com.major_project.digital_library.jwt.JWTService;
import com.major_project.digital_library.model.AuthModel;
import com.major_project.digital_library.model.request_model.LoginRequestModel;
import com.major_project.digital_library.model.request_model.SignupRequestModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.model.response_model.UserResponseModel;
import com.major_project.digital_library.service.*;
import com.major_project.digital_library.service.other.EmailService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/auth")
public class AuthController {
    private final IAuthService authService;
    private final ModelMapper modelMapper;
    private final IUserService userService;
    private final IOrganizationService organizationService;
    private final IRoleService roleService;
    private final EmailService emailService;
    private final IVerificationCodeService verificationCodeService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Value("${API_KEY}")
    private String SECRET_KEY;

    @Autowired
    public AuthController(IAuthService authService, ModelMapper modelMapper, IUserService userService, IOrganizationService organizationService, IRoleService roleService, EmailService emailService, IVerificationCodeService verificationCodeService, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.authService = authService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.organizationService = organizationService;
        this.roleService = roleService;
        this.emailService = emailService;
        this.verificationCodeService = verificationCodeService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestModel signupRequest) {
        UserResponseModel userResponseModel = authService.signup(signupRequest);

        ResponseModel signupResponse = new ResponseModel().builder()
                .status(200)
                .error(false)
                .message("Sign up successfully. Please log in.")
                .data(userResponseModel)
                .build();
        return ResponseEntity.ok(signupResponse);
    }

    @PostMapping("/signup/google")
    public ResponseEntity<?> signupWithGoogle(@Valid @RequestBody Map<String, String> signupRequest) {
        AuthModel authModel = authService.signupWithGoogle(signupRequest);

        ResponseModel signupResponse = new ResponseModel().builder()
                .status(200)
                .error(false)
                .message("Sign up with Google successfully.")
                .data(authModel)
                .build();
        return ResponseEntity.ok(signupResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestModel loginRequestModel) {
        AuthModel authResponse = authService.login(loginRequestModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Login successfully")
                .data(authResponse)
                .build());
    }

    @PostMapping("/login/google")
    public ResponseEntity<?> loginWithGoogle(@Valid @RequestBody Map<String, String> loginRequest) {
        AuthModel authResponse = authService.loginWithGoogle(loginRequest);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Login with google successfully")
                .data(authResponse)
                .build());
    }

    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(@RequestParam String email, @RequestParam String type) {
        authService.sendEmail(email, type);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message(type.equals("reset")
                        ? "Send reset password code successfully"
                        : "Send authentication code successfully")
                .build());
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String email, @RequestParam int code, @RequestParam String type) {
        AuthModel authModel = authService.verifyUser(email, code, type);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Authenticated user successfully")
                .data(authModel)
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody Map<String, String> refreshToken) {
        Map<String, String> accessTokenResponse = authService.refresh(refreshToken);

        ResponseModel refreshResponse = new ResponseModel().builder()
                .status(200)
                .error(false)
                .message("Renew access token successfully")
                .data(accessTokenResponse)
                .build();
        return ResponseEntity.ok(refreshResponse);

    }
}
