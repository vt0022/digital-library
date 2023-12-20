package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.*;
import com.major_project.digital_library.exception_handler.exception.ModelNotFoundException;
import com.major_project.digital_library.model.FileModel;
import com.major_project.digital_library.model.request_model.PasswordRequestModel;
import com.major_project.digital_library.model.request_model.PasswordResetRequestModel;
import com.major_project.digital_library.model.request_model.UserProfileRequestModel;
import com.major_project.digital_library.model.request_model.UserRequestModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.model.response_model.UserResponseModel;
import com.major_project.digital_library.service.IOrganizationService;
import com.major_project.digital_library.service.IRoleService;
import com.major_project.digital_library.service.IUserService;
import com.major_project.digital_library.util.GoogleDriveUpload;
import com.major_project.digital_library.util.StringHandler;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final IUserService userService;
    private final IOrganizationService organizationService;
    private final IRoleService roleService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final GoogleDriveUpload googleDriveUpload;
    private final StringHandler stringHandler;

    @Autowired
    public UserController(IUserService userService, IOrganizationService organizationService, IRoleService roleService, ModelMapper modelMapper, PasswordEncoder passwordEncoder, GoogleDriveUpload googleDriveUpload, StringHandler stringHandler) {
        this.userService = userService;
        this.organizationService = organizationService;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.googleDriveUpload = googleDriveUpload;
        this.stringHandler = stringHandler;
    }

    @Operation(summary = "Cập nhật mật khẩu",
            description = "Người dùng kiểm tra và cập nhật mật khẩu")
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordRequestModel passwordRequestModel) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(passwordRequestModel.getOldPassword(), user.getPassword())) {
            if (passwordRequestModel.getNewPassword().equals(passwordRequestModel.getConfirmPassword())) {
                user.setPassword(passwordRequestModel.getNewPassword());
                user = userService.save(user);
                UserResponseModel userResponseModel = modelMapper.map(user, UserResponseModel.class);
                return ResponseEntity.ok(new ResponseModel().builder()
                        .status(200)
                        .error(false)
                        .message("Password changed successfully")
                        .data(userResponseModel)
                        .build());
            } else {
                throw new RuntimeException("Passwords not matched");
            }
        } else {
            throw new RuntimeException("Password incorrect");
        }
    }

    @Operation(summary = "Khôi phục mật khẩu",
            description = "Người dùng khôi phục mật khẩu")
    @PutMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        User user = userService.findByEmailAndIsDeleted(passwordResetRequestModel.getEmail(), false).orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordResetRequestModel.getNewPassword().equals(passwordResetRequestModel.getConfirmPassword())) {
            user.setPassword(passwordResetRequestModel.getNewPassword());
            userService.save(user);
            return ResponseEntity.ok(new ResponseModel().builder()
                    .status(200)
                    .error(false)
                    .message("Password reset successfully")
                    .build());
        } else {
            throw new RuntimeException("Passwords not matched");
        }

    }

    @Operation(summary = "Lấy danh sách tất cả người dùng")
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userService.findAll(pageable);

        Page<UserResponseModel> userModels = users.map(this::convertToUserModel);
        ;
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all users successfully")
                .data(userModels)
                .build());

    }

    @Operation(summary = "Lấy danh sách tất cả người dùng")
    @GetMapping("/organizations/{slug}")
    public ResponseEntity<?> getAllUsersByOrganization(@PathVariable String slug,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        Organization organization = organizationService.findBySlug(slug).orElseThrow(() -> new RuntimeException("Organization not found"));

        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userService.findByOrganization(organization, pageable);

        Page<UserResponseModel> userModels = users.map(this::convertToUserModel);
        ;
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all users successfully")
                .data(userModels)
                .build());

    }

    @Operation(summary = "Lấy thông tin người dùng")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable UUID userId) {
        User user = userService.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found"));
        UserResponseModel userResponseModel = modelMapper.map(user, UserResponseModel.class);

        int totalDocuments = user.getUploadedDocuments().size();
        int totalViews = (int) user.getUploadedDocuments().stream()
                .mapToLong(Document::getTotalView)
                .sum();
        int totalLikes = (int) user.getUploadedDocuments().stream()
                .flatMap(document -> document.getFavorites().stream()) // Chuyển từng list like của mỗi document thành một stream
                .filter(Favorite::isLiked) // Lọc những like có trạng thái là true
                .count(); // Đếm số lượng lượt like có trạng thái là true

        userResponseModel.setTotalDocuments(totalDocuments);
        userResponseModel.setTotalViews(totalViews);
        userResponseModel.setTotalLikes(totalLikes);

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
        Optional<User> user = userService.findByEmail(userRequestModel.getEmail());
        if (user.isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        User newUser = modelMapper.map(userRequestModel, User.class);

        if (userRequestModel.getOrgId() != null && userRequestModel.getOrgId().toString() != "") {
            Organization organization = organizationService.findById(userRequestModel.getOrgId()).orElseThrow(
                    () -> new ModelNotFoundException("Organization not found"));
            newUser.setOrganization(organization);
        }
        Role role = roleService.findById(userRequestModel.getRoleId()).orElseThrow(
                () -> new ModelNotFoundException("Role not found"));
        newUser.setRole(role);

        if (!userRequestModel.getPassword().equals(userRequestModel.getConfirmPassword())) {
            throw new RuntimeException("Password not match");
        }

        FileModel gd = googleDriveUpload.uploadImage(file, stringHandler.getEmailUsername(newUser.getEmail()), null);
        newUser.setImage(gd.getViewUrl());

        newUser = userService.save(newUser);

        UserResponseModel userResponseModel = modelMapper.map(newUser, UserResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create user successfully")
                .data(userResponseModel)
                .build());
    }

    @Operation(summary = "Update mật khẩu")
    @PreAuthorize("permitAll")
    @PostMapping("/pass/all")
    public ResponseEntity<?> updatePasswordForAll() {
        Pageable pageable = PageRequest.of(0, 100);
        List<User> users = userService.findAll(pageable).getContent();
        for (User user : users) {
            String password = stringHandler.getEmailUsername(user.getEmail());
            user.setPassword(password);
            userService.save(user);
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update password successfully")
                .build());
    }

    @Operation(summary = "Cập nhật người dùng")
    @PutMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(@PathVariable UUID userId,
                                        @RequestPart(value = "file", required = false) MultipartFile file,
                                        @RequestPart(value = "user") UserRequestModel userRequestModel) {
        User user = userService.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found"));

        Optional<User> tempUser = userService.findByEmail(userRequestModel.getEmail());
        if (tempUser.isPresent())
            if (tempUser.get().getUserId() != user.getUserId())
                throw new RuntimeException("Email already registered");

        user.setFirstName(userRequestModel.getFirstName());
        user.setLastName(userRequestModel.getLastName());
        user.setDateOfBirth(userRequestModel.getDateOfBirth());
        user.setGender(userRequestModel.getGender());
        user.setPhone(userRequestModel.getPhone());
        user.setEmail(userRequestModel.getEmail());

        if (userRequestModel.getOrgId().toString() != "") {
            Organization organization = organizationService.findById(userRequestModel.getOrgId()).orElseThrow(
                    () -> new ModelNotFoundException("Organization not found"));
            user.setOrganization(organization);
        }
        Role role = roleService.findById(userRequestModel.getRoleId()).orElseThrow(
                () -> new ModelNotFoundException("Role not found"));
        user.setRole(role);

        if (file != null) {
            if (user.getImage() != null) {
                FileModel gd = googleDriveUpload.uploadImage(file, stringHandler.getEmailUsername(user.getEmail()), stringHandler.getFileId(user.getImage()));
                user.setImage(gd.getViewUrl());
            } else {
                FileModel gd = googleDriveUpload.uploadImage(file, stringHandler.getEmailUsername(user.getEmail()), null);
                user.setImage(gd.getViewUrl());
            }
        }

        // Password changed
        if (userRequestModel.getPassword().trim() == "") {
            user = userService.update(user);
        } else {
            if (!userRequestModel.getPassword().equals(userRequestModel.getConfirmPassword())) {
                throw new RuntimeException("Passwords not match");
            } else {
                user.setPassword(userRequestModel.getPassword());
                user = userService.save(user);
            }
        }

        UserResponseModel userResponseModel = modelMapper.map(user, UserResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update user successfully")
                .data(userResponseModel)
                .build());
    }

    @Operation(summary = "Xoá người dùng")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getUploadedDocuments().size() > 0) {
            user.setDeleted(true);
            userService.update(user);
        } else {
            userService.deleteById(userId);
        }
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Delete user successfully")
                .build());
    }

    @Operation(summary = "Lấy thông tin cá nhân")
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));
        UserResponseModel userResponseModel = modelMapper.map(user, UserResponseModel.class);

        int totalDocuments = user.getUploadedDocuments().size();
        int totalViews = (int) user.getUploadedDocuments().stream()
                .mapToLong(Document::getTotalView)
                .sum();
        int totalLikes = (int) user.getUploadedDocuments().stream()
                .flatMap(document -> document.getFavorites().stream()) // Chuyển từng list like của mỗi document thành một stream
                .filter(Favorite::isLiked) // Lọc những like có trạng thái là true
                .count(); // Đếm số lượng lượt like có trạng thái là true

        userResponseModel.setTotalDocuments(totalDocuments);
        userResponseModel.setTotalViews(totalViews);
        userResponseModel.setTotalLikes(totalLikes);

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
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        Optional<User> tempUser = userService.findByEmail(userProfileRequestModel.getEmail());
        if (tempUser.isPresent())
            if (tempUser.get().getUserId() != user.getUserId())
                throw new RuntimeException("Email already registered");

        user.setFirstName(userProfileRequestModel.getFirstName());
        user.setLastName(userProfileRequestModel.getLastName());
        user.setDateOfBirth(userProfileRequestModel.getDateOfBirth());
        user.setGender(userProfileRequestModel.getGender());
        user.setPhone(userProfileRequestModel.getPhone());
        user.setEmail(userProfileRequestModel.getEmail());

        user = userService.update(user);

        UserResponseModel userResponseModel = modelMapper.map(user, UserResponseModel.class);
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
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getImage() != null) {
            FileModel gd = googleDriveUpload.uploadImage(file, stringHandler.getEmailUsername(user.getEmail()), stringHandler.getFileId(user.getImage()));
            user.setImage(gd.getViewUrl());
        } else {
            FileModel gd = googleDriveUpload.uploadImage(file, stringHandler.getEmailUsername(user.getEmail()), null);
            user.setImage(gd.getViewUrl());
        }

        user = userService.update(user);
        UserResponseModel userResponseModel = modelMapper.map(user, UserResponseModel.class);
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
                                            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.findLatestUsers(pageable);

        Page<UserResponseModel> userModels = users.map(this::convertToUserModel);
        ;
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all users successfully")
                .data(userModels)
                .build());

    }

    @Operation(summary = "Lấy danh sách người dùng mới trong tháng của một trường")
    @GetMapping("/organizations/{slug}/latest")
    public ResponseEntity<?> getLatestUsersByOrganization(@PathVariable String slug,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size) {
        Organization organization = organizationService.findBySlug(slug).orElseThrow(() -> new RuntimeException("Organization not found"));
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.findLatestUsersByOrganization(organization, pageable);

        Page<UserResponseModel> userModels = users.map(this::convertToUserModel);
        ;
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all users successfully")
                .data(userModels)
                .build());

    }

    private UserResponseModel convertToUserModel(Object o) {
        UserResponseModel userResponseModel = modelMapper.map(o, UserResponseModel.class);
        return userResponseModel;
    }
}
