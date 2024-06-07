package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.request_model.PasswordRequestModel;
import com.major_project.digital_library.model.request_model.PasswordResetRequestModel;
import com.major_project.digital_library.model.request_model.UserProfileRequestModel;
import com.major_project.digital_library.model.request_model.UserRequestModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.model.response_model.UserReputationResponseModel;
import com.major_project.digital_library.model.response_model.UserResponseModel;
import com.major_project.digital_library.service.IBadgeService;
import com.major_project.digital_library.service.IOrganizationService;
import com.major_project.digital_library.service.IRoleService;
import com.major_project.digital_library.service.IUserService;
import com.major_project.digital_library.service.other.GoogleDriveService;
import com.major_project.digital_library.util.StringHandler;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v2/users")
public class UserController {
    private final IUserService userService;
    private final IOrganizationService organizationService;
    private final IRoleService roleService;
    private final IBadgeService badgeService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final GoogleDriveService googleDriveService;
    private final StringHandler stringHandler;

    @Autowired
    public UserController(IUserService userService, IOrganizationService organizationService, IRoleService roleService, IBadgeService badgeService, ModelMapper modelMapper, PasswordEncoder passwordEncoder, GoogleDriveService googleDriveService, StringHandler stringHandler) {
        this.userService = userService;
        this.organizationService = organizationService;
        this.roleService = roleService;
        this.badgeService = badgeService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.googleDriveService = googleDriveService;
        this.stringHandler = stringHandler;
    }

    @Operation(summary = "Cập nhật mật khẩu",
            description = "Người dùng kiểm tra và cập nhật mật khẩu")
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordRequestModel passwordRequestModel) {
        UserResponseModel userResponseModel = userService.changePassword(passwordRequestModel);

        return ResponseEntity.ok(new ResponseModel().builder()
                .status(200)
                .error(false)
                .message("Password changed successfully")
                .data(userResponseModel)
                .build());
    }

    @Operation(summary = "Khôi phục mật khẩu",
            description = "Người dùng khôi phục mật khẩu")
    @PutMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        userService.resetPassword(passwordResetRequestModel);

        return ResponseEntity.ok(new ResponseModel().builder()
                .status(200)
                .error(false)
                .message("Password reset successfully")
                .build());
    }

    @Operation(summary = "Lấy danh sách tất cả người dùng")
    @GetMapping()
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size,
                                         @RequestParam(defaultValue = "all") String disabled,
                                         @RequestParam(defaultValue = "all") String gender,
                                         @RequestParam(defaultValue = "all") String organization,
                                         @RequestParam(defaultValue = "all") String role,
                                         @RequestParam(defaultValue = "") String s) {
        Page<UserResponseModel> userModels = userService.getAllUsers(page, size, disabled, gender, organization, role, s);
        ;
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all users successfully")
                .data(userModels)
                .build());

    }

    @Operation(summary = "Lấy danh sách tất cả người dùng của một tổ chức")
    @GetMapping("/organizations/{slug}")
    public ResponseEntity<?> getAllUsersByOrganization(@PathVariable String slug,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size,
                                                       @RequestParam(defaultValue = "all") String disabled,
                                                       @RequestParam(defaultValue = "all") String gender,
                                                       @RequestParam(defaultValue = "all") String role,
                                                       @RequestParam(defaultValue = "") String s) {
        Page<UserResponseModel> userModels = userService.getAllUsersByOrganization(slug, page, size, disabled, gender, role, s);
        ;
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all users of organization successfully")
                .data(userModels)
                .build());
    }

    @Operation(summary = "Lấy thông tin người dùng")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable UUID userId) {
        UserResponseModel userResponseModel = userService.getUser(userId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get user successfully")
                .data(userResponseModel)
                .build());
    }

    @Operation(summary = "Tạo người dùng")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createUser(@RequestPart("file") MultipartFile file,
                                        @RequestPart("user") UserRequestModel userRequestModel) {
        UserResponseModel userResponseModel = userService.createUser(file, userRequestModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create user successfully")
                .data(userResponseModel)
                .build());
    }

    @Operation(summary = "Cập nhật người dùng")
    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(@PathVariable UUID userId,
                                        @RequestPart(value = "file", required = false) MultipartFile file,
                                        @RequestPart(value = "user") UserRequestModel userRequestModel) {
        UserResponseModel userResponseModel = userService.updateUser(userId, file, userRequestModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update user successfully")
                .data(userResponseModel)
                .build());
    }

    @Operation(summary = "Chặn người dùng")
    @PutMapping("/{userId}/disable")
    public ResponseEntity<?> disableUser(@PathVariable UUID userId) {
        userService.disableUser(userId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Disable user successfully")
                .build());
    }

    @Operation(summary = "Bỏ chặn người dùng")
    @PutMapping("/{userId}/enable")
    public ResponseEntity<?> enableUser(@PathVariable UUID userId) {
        userService.enableUser(userId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Enable user successfully")
                .build());
    }

    @Operation(summary = "Lấy thông tin cá nhân")
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        UserResponseModel userResponseModel = userService.getProfile();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get profile successfully")
                .data(userResponseModel)
                .build());
    }

    @Operation(summary = "Cập nhật thông tin cá nhân")
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserProfileRequestModel userProfileRequestModel) {
        UserResponseModel userResponseModel = userService.updateProfile(userProfileRequestModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update profile successfully")
                .data(userResponseModel)
                .build());
    }

    @Operation(summary = "Cập nhật ảnh đại diện")
    @PutMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateAvatar(@RequestPart("avatar") MultipartFile file) {
        UserResponseModel userResponseModel = userService.updateAvatar(file);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update profile successfully")
                .data(userResponseModel)
                .build());
    }

    @Operation(summary = "Lấy danh sách người dùng mới trong tháng")
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestUsers(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size,
                                            @RequestParam(defaultValue = "all") String disabled,
                                            @RequestParam(defaultValue = "all") String gender,
                                            @RequestParam(defaultValue = "all") String organization,
                                            @RequestParam(defaultValue = "all") String role,
                                            @RequestParam(defaultValue = "") String s) {
        Page<UserResponseModel> userModels = userService.getLatestUsers(page, size, disabled, gender, organization, role, s);
        ;
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get latest users successfully")
                .data(userModels)
                .build());

    }

    @Operation(summary = "Lấy danh sách người dùng mới trong tháng của một trường")
    @GetMapping("/organizations/{slug}/latest")
    public ResponseEntity<?> getLatestUsersByOrganization(@PathVariable String slug,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size,
                                                          @RequestParam(defaultValue = "all") String disabled,
                                                          @RequestParam(defaultValue = "all") String gender,
                                                          @RequestParam(defaultValue = "all") String role,
                                                          @RequestParam(defaultValue = "") String s) {
        Page<UserResponseModel> userModels = userService.getLatestUsersByOrganization(slug, page, size, disabled, gender, role, s);
        ;
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all users successfully")
                .data(userModels)
                .build());

    }

    @Operation(summary = "Lấy danh sách người dùng kèm chỉ số danh tiếng")
    @GetMapping("/ranking")
    public ResponseEntity<?> getUserReputation(@RequestParam(defaultValue = "0") int month,
                                               @RequestParam(defaultValue = "0") int year,
                                               @RequestParam(defaultValue = "") String s,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        Page<UserReputationResponseModel> userModels = userService.getUserReputation(s, month, year, page, size);
        ;
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get user reputation successfully")
                .data(userModels)
                .build());
    }
}
