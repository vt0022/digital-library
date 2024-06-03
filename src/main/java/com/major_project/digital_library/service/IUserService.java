package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.PasswordRequestModel;
import com.major_project.digital_library.model.request_model.PasswordResetRequestModel;
import com.major_project.digital_library.model.request_model.UserProfileRequestModel;
import com.major_project.digital_library.model.request_model.UserRequestModel;
import com.major_project.digital_library.model.response_model.UserReputationResponseModel;
import com.major_project.digital_library.model.response_model.UserResponseModel;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IUserService {

    <S extends User> S save(S entity);

    <S extends User> S update(S entity);

    User findLoggedInUser();

    UserResponseModel changePassword(PasswordRequestModel passwordRequestModel);

    void resetPassword(PasswordResetRequestModel passwordResetRequestModel);

    Page<UserResponseModel> getAllUsers(int page, int size, String disabled, String gender, String organization, String role, String s);

    Page<UserResponseModel> getAllUsersByOrganization(String slug, int page, int size, String disabled, String gender, String role, String s);

    UserResponseModel getUser(UUID userId);

    UserResponseModel createUser(MultipartFile file, UserRequestModel userRequestModel);

    UserResponseModel updateUser(UUID userId, MultipartFile file, UserRequestModel userRequestModel);

    void disableUser(UUID userId);

    void enableUser(UUID userId);

    UserResponseModel getProfile();

    UserResponseModel updateProfile(UserProfileRequestModel userProfileRequestModel);

    UserResponseModel updateAvatar(MultipartFile file);

    Page<UserResponseModel> getLatestUsers(int page, int size, String disabled, String gender, String organization, String role, String s);

    Page<UserResponseModel> getLatestUsersByOrganization(String slug, int page, int size, String disabled, String gender, String role, String s);

    Page<UserReputationResponseModel> getUserReputation(String s, int page, int size);
}
