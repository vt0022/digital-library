package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.Role;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.exception_handler.exception.ModelNotFoundException;
import com.major_project.digital_library.model.request_model.PasswordRequestModel;
import com.major_project.digital_library.model.request_model.UserProfileRequestModel;
import com.major_project.digital_library.model.request_model.UserRequestModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.model.response_model.UserResponseModel;
import com.major_project.digital_library.service.IOrganizationService;
import com.major_project.digital_library.service.IRoleService;
import com.major_project.digital_library.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    public UserController(IUserService userService, IOrganizationService organizationService, IRoleService roleService, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.organizationService = organizationService;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Cập nhật mật khẩu",
            description = "Người dùng kiểm tra và cập nhật mật khẩu")
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@PathVariable UUID userId, @RequestBody PasswordRequestModel passwordRequestModel) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(passwordRequestModel.getOldPassword(), user.getPassword())) {
            if (passwordRequestModel.getNewPassword().equals(passwordRequestModel.getVerifiedPassword())) {
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

    @Operation(summary = "Lấy thông tin người dùng")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable UUID userId) {
        User user = userService.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found"));
        UserResponseModel userResponseModel = modelMapper.map(user, UserResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get user successfully")
                .data(userResponseModel)
                .build());
    }

    @Operation(summary = "Tạo người dùng")
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequestModel userRequestModel) {
        Optional<User> user = userService.findByEmail(userRequestModel.getEmail());
        if (user.isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        User newUser = modelMapper.map(userRequestModel, User.class);

        if (userRequestModel.getOrgId() != null || userRequestModel.getOrgId().toString() != "") {
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

        newUser = userService.save(newUser);

        UserResponseModel userResponseModel = modelMapper.map(newUser, UserResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create user successfully")
                .data(userResponseModel)
                .build());
    }

    @Operation(summary = "Cập nhật người dùng")
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable UUID userId, @RequestBody UserRequestModel userRequestModel) {
        User user = userService.findById(userId).orElseThrow(
                () -> new RuntimeException("Email already registered"));
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
        if (user.getFavorites().size() > 0 || user.getSaves().size() > 0 || user.getReviews().size() > 0) {
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
        modelMapper.map(userProfileRequestModel, user);
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
    @PutMapping("/avatar")
    public ResponseEntity<?> updateAvatar(@RequestPart("avatar") MultipartFile file) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));
        user = userService.update(user);
        UserResponseModel userResponseModel = modelMapper.map(user, UserResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update profile successfully")
                .data(userResponseModel)
                .build());
    }

    private UserResponseModel convertToUserModel(Object o) {
        UserResponseModel userResponseModel = modelMapper.map(o, UserResponseModel.class);
        return userResponseModel;
    }
}
