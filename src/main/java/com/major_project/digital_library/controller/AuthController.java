package com.major_project.digital_library.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.major_project.digital_library.auth.JWTService;
import com.major_project.digital_library.constant.Constant;
import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.Role;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.LoginRequestModel;
import com.major_project.digital_library.model.request_model.SignupRequestModel;
import com.major_project.digital_library.model.response_model.AuthResponseModel;
import com.major_project.digital_library.model.response_model.FullResponseModel;
import com.major_project.digital_library.model.response_model.ShortResponseModel;
import com.major_project.digital_library.service.OrganizationService;
import com.major_project.digital_library.service.RoleService;
import com.major_project.digital_library.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/v1/public/auth")
public class AuthController {
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final OrganizationService organizationService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Autowired
    public AuthController(ModelMapper modelMapper, UserService userService, OrganizationService organizationService, RoleService roleService, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.organizationService = organizationService;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestModel signupRequest) {
        try {
            Optional<User> user = userService.findByEmail(signupRequest.getEmail());
            if (user.isPresent()) {
                return ResponseEntity.ok(ShortResponseModel.builder()
                        .status(400)
                        .error(true)
                        .message("Email registered!")
                        .build());
            } else {
                // Organization
                Organization organization = organizationService.findById(signupRequest.getOrgId()).orElse(null);
                // Role
                Role role = roleService.findById(signupRequest.getRoleId()).orElse(null);
                User newUser = modelMapper.map(signupRequest, User.class);
                newUser.setOrganization(organization);
                newUser.setRole(role);
                userService.save(newUser);

                FullResponseModel signupResponse = new FullResponseModel().builder()
                        .status(200)
                        .error(false)
                        .message("Sign up successfully. Please log in.")
                        .data(newUser)
                        .build();
                return ResponseEntity.ok(signupResponse);
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ShortResponseModel.builder()
                    .status(400)
                    .error(true)
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestModel loginRequestModel) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestModel.getEmail(), loginRequestModel.getPassword()));
            User user = userService.findByEmail(loginRequestModel.getEmail()).orElse(null);

            if (user != null) {
                Role role = user.getRole();

                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
                // Generate JWT
                var jwtToken = jwtService.generateToken(user, authorities);
                var jwtRefreshToken = jwtService.generateRefreshToken(user);

                AuthResponseModel authResponse = AuthResponseModel.builder()
                        .token(jwtToken)
                        .refreshToken(jwtRefreshToken)
                        .email(user.getEmail())
                        .role(user.getRole().getRoleName())
                        .build();

                return ResponseEntity.ok(FullResponseModel.builder()
                        .status(200)
                        .error(false)
                        .message("Login successfully")
                        .data(authResponse)
                        .build());
            } else {
                return ResponseEntity.ok(ShortResponseModel.builder()
                        .status(400)
                        .error(true)
                        .message("Email not registered")
                        .build());
            }
        } catch (BadCredentialsException e) {
            return ResponseEntity.ok(ShortResponseModel.builder()
                    .status(401)
                    .error(true)
                    .message("Invalid login credentials")
                    .build());
        } catch (AuthenticationException e) {
            return ResponseEntity.ok(ShortResponseModel.builder()
                    .status(400)
                    .error(true)
                    .message(e.getMessage().toString())
                    .build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody Map<String, String> refreshToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(Constant.SECRET_KEY.getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(refreshToken.get("refreshToken"));
            String username = decodedJWT.getSubject();

            User user = userService.findByEmail(username).orElse(null);

            if (user != null) {
                Role role = user.getRole();

                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(role.getRoleName()));

                String accessToken = jwtService.generateToken(user, authorities);

                Map<String, String> accessTokenResponse = new HashMap<>();
                accessTokenResponse.put("token", accessToken);

                FullResponseModel refreshResponse = new FullResponseModel().builder()
                        .status(200)
                        .error(false)
                        .message("Renew access token successfully")
                        .data(accessTokenResponse)
                        .build();
                return ResponseEntity.ok(refreshResponse);
            } else {
                return ResponseEntity.ok(ShortResponseModel.builder()
                        .status(400)
                        .error(true)
                        .message("User not found")
                        .build());
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ShortResponseModel.builder()
                    .status(400)
                    .error(true)
                    .message(e.getMessage().toString())
                    .build());
        }
    }
}
