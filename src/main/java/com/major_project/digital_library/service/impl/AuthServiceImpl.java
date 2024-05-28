package com.major_project.digital_library.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.Role;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.entity.VerificationCode;
import com.major_project.digital_library.jwt.JWTService;
import com.major_project.digital_library.model.AuthModel;
import com.major_project.digital_library.model.request_model.LoginRequestModel;
import com.major_project.digital_library.model.request_model.SignupRequestModel;
import com.major_project.digital_library.model.response_model.UserResponseModel;
import com.major_project.digital_library.repository.IOrganizationRepository;
import com.major_project.digital_library.repository.IRoleRepository;
import com.major_project.digital_library.repository.IUserRepository;
import com.major_project.digital_library.repository.IVerificationCodeRepository;
import com.major_project.digital_library.service.IAuthService;
import com.major_project.digital_library.service.IUserService;
import com.major_project.digital_library.service.other.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class AuthServiceImpl implements IAuthService {
    private final ModelMapper modelMapper;
    private final IUserService userService;
    private final IOrganizationRepository organizationRepository;
    private final IRoleRepository roleRepository;
    private final EmailService emailService;
    private final IVerificationCodeRepository verificationCodeRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final IUserRepository userRepositoty;

    @Value("${API_KEY}")
    private String SECRET_KEY;

    @Autowired
    public AuthServiceImpl(ModelMapper modelMapper, IUserService userService, IOrganizationRepository organizationRepository, IRoleRepository roleRepository, EmailService emailService, IVerificationCodeRepository verificationCodeRepository, AuthenticationManager authenticationManager, JWTService jwtService, IUserRepository userRepositoty) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.organizationRepository = organizationRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.verificationCodeRepository = verificationCodeRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepositoty = userRepositoty;
    }


    @Override
    public UserResponseModel signup(SignupRequestModel signupRequest) {
        Optional<User> user = userRepositoty.findByEmail(signupRequest.getEmail());

        if (user.isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            throw new RuntimeException("Password not matched");
        }

        Organization organization = organizationRepository.findById(signupRequest.getOrgId()).orElseThrow(() -> new RuntimeException("Organization not found"));
        Role role = roleRepository.findById(UUID.fromString("c0a801b9-8ac0-1a60-818a-c04a8f950035")).orElseThrow(() -> new RuntimeException("Role not found"));
        User newUser = modelMapper.map(signupRequest, User.class);
        newUser.setOrganization(organization);
        newUser.setRole(role);
        newUser = userService.save(newUser);

        UserResponseModel userResponseModel = modelMapper.map(newUser, UserResponseModel.class);

        return userResponseModel;
    }

    @Override
    public AuthModel signupWithGoogle(Map<String, String> signupRequest) {
        GoogleCredential credential = new GoogleCredential().setAccessToken(signupRequest.get("accessToken"));
        Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName(
                "Oauth2").build();

        try {
            Userinfo userinfo = oauth2.userinfo().get().execute();

            Organization organization = organizationRepository.findById(UUID.fromString(signupRequest.get("org"))).orElseThrow(() -> new RuntimeException("Organization not found"));
            Role role = roleRepository.findById(UUID.fromString("c0a801b9-8ac0-1a60-818a-c04a8f950035")).orElseThrow(() -> new RuntimeException("Role not found"));

            User user = new User();
            user.setEmail(userinfo.getEmail());
            user.setPassword(userinfo.getEmail() + userinfo.getGivenName());
            user.setFirstName(userinfo.getGivenName());
            user.setLastName(userinfo.getFamilyName());
            user.setAuthenticated(true);
            user.setImage(userinfo.getPicture());
            user.setRole(role);
            user.setOrganization(organization);
            user = userService.save(user);

            return createAuthModel(user);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public AuthModel login(LoginRequestModel loginRequestModel) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestModel.getEmail(), loginRequestModel.getPassword()));
        } catch (Exception e) {
            Optional<User> optionalUser = userRepositoty.findByEmail(loginRequestModel.getEmail());
            if (!optionalUser.isPresent())
                throw new BadCredentialsException("Email not registered");
            else
                throw new BadCredentialsException("Wrong password");
        }

        User user = userRepositoty.findByEmail(loginRequestModel.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isDisabled())
            throw new RuntimeException("Account disabled");
        else if (!user.isAuthenticated())
            throw new RuntimeException("Account needs activated");

        return createAuthModel(user);
    }

    @Override
    public AuthModel loginWithGoogle(Map<String, String> loginRequest) {
        GoogleCredential credential = new GoogleCredential().setAccessToken(loginRequest.get("accessToken"));
        Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName(
                "Oauth2").build();

        try {
            Userinfo userinfo = oauth2.userinfo().get().execute();

            Optional<User> optionalUser = userRepositoty.findByEmail(userinfo.getEmail());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (user.isDisabled())
                    throw new RuntimeException("Account disabled");
                else {
                    if (!user.isAuthenticated()) {
                        user.setAuthenticated(true);
                        user = userRepositoty.save(user);
                    }

                    return createAuthModel(user);
                }
            } else {
                throw new BadCredentialsException("Email not registered");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void sendEmail(String email, String type) {
        User user = userRepositoty.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not registered"));

        if (user.isDisabled())
            throw new RuntimeException("Account disabled");

        Random random = new Random();
        int code = random.nextInt(900000) + 100000;

        VerificationCode verificationCode = new VerificationCode();
        Optional<VerificationCode> verificationCodeOptional = verificationCodeRepository.findByUser(user);
        if (verificationCodeOptional.isPresent()) {
            verificationCode = verificationCodeOptional.get();
            verificationCode.setCode(code);
        } else {
            verificationCode.setCode(code);
            verificationCode.setUser(user);
        }
        verificationCode.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        verificationCode.setExpiredAt(new Timestamp(System.currentTimeMillis() + 15 * 60 * 1000));
        verificationCode.setExpired(false);
        verificationCodeRepository.save(verificationCode);

        if (type.equals("reset"))
            emailService.sendEmail(
                    email,
                    "RESET PASSWORD",
                    code,
                    "reset_password_email");
        else // register
            emailService.sendEmail(
                    email,
                    "VERIFY ON REGISTER",
                    code,
                    "verify_on_signup_email");
    }

    @Override
    public AuthModel verifyUser(String email, int code, String type) {
        User user = userRepositoty.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isDisabled())
            throw new RuntimeException("Account disabled");

        VerificationCode verificationCode = verificationCodeRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Invalid verification code"));

        if (verificationCode.getCode() == code) {
            if (verificationCode.isExpired())
                throw new RuntimeException("Verification code is expired");
            else {
                if (verificationCode.getExpiredAt().getTime() > System.currentTimeMillis()) {
                    verificationCode.setExpired(true);
                    verificationCodeRepository.save(verificationCode);

                    if (type.equals("register")) {
                        user.setAuthenticated(true);
                        user = userService.update(user);

                        return createAuthModel(user);
                    } else {
                        return null;
                    }
                } else throw new RuntimeException("Verification code is expired");
            }
        } else throw new RuntimeException("Wrong verification code");
    }

    @Override
    public Map<String, String> refresh(Map<String, String> refreshToken) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken.get("refreshToken"));
        String username = decodedJWT.getSubject().split(" ")[0]; // Get email

        User user = userRepositoty.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Email not registered"));

        String accessToken = jwtService.generateToken(user);

        Map<String, String> accessTokenResponse = new HashMap<>();
        accessTokenResponse.put("accessToken", accessToken);

        return refreshToken;

    }

    public AuthModel createAuthModel(User user) {
        // Generate JWT
        var jwtToken = jwtService.generateToken(user);
        var jwtRefreshToken = jwtService.generateRefreshToken(user);

        UserResponseModel userResponseModel = modelMapper.map(user, UserResponseModel.class);

        AuthModel authResponse = AuthModel.builder()
                .accessToken(jwtToken)
                .refreshToken(jwtRefreshToken)
                .profile(userResponseModel)
                .build();

        return authResponse;
    }
}
