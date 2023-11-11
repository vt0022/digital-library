package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.PasswordRequestModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.model.response_model.UserResponseModel;
import com.major_project.digital_library.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final IUserService userService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(IUserService userService, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Cập nhật mật khẩu (người dùng)",
            description = "Người dùng kiểm tra và cập nhật mật khẩu")
    @PatchMapping("/{userId}/password")
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

//    @Operation(summary = "Cập nhật thông tin cá nhân")
//    @PutMapping()
}
