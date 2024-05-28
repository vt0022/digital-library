package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.Role;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.exception_handler.exception.ModelNotFoundException;
import com.major_project.digital_library.exception_handler.exception.UserAuthenticationException;
import com.major_project.digital_library.model.FileModel;
import com.major_project.digital_library.model.lean_model.BadgeLeanModel;
import com.major_project.digital_library.model.request_model.PasswordRequestModel;
import com.major_project.digital_library.model.request_model.PasswordResetRequestModel;
import com.major_project.digital_library.model.request_model.UserProfileRequestModel;
import com.major_project.digital_library.model.request_model.UserRequestModel;
import com.major_project.digital_library.model.response_model.UserResponseModel;
import com.major_project.digital_library.repository.IOrganizationRepository;
import com.major_project.digital_library.repository.IRoleRepository;
import com.major_project.digital_library.repository.IUserRepository;
import com.major_project.digital_library.service.IBadgeService;
import com.major_project.digital_library.service.IUserService;
import com.major_project.digital_library.service.other.GoogleDriveService;
import com.major_project.digital_library.util.StringHandler;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
public class UserServiceImpl implements IUserService {
    private final IUserRepository userRepository;
    private final IOrganizationRepository organizationRepository;
    private final IRoleRepository roleRepository;
    private final IBadgeService badgeService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final GoogleDriveService googleDriveService;
    private final StringHandler stringHandler;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository, IOrganizationRepository organizationRepository, IRoleRepository roleRepository, IBadgeService badgeService, ModelMapper modelMapper, PasswordEncoder passwordEncoder, GoogleDriveService googleDriveService, StringHandler stringHandler) {

        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.roleRepository = roleRepository;
        this.badgeService = badgeService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.googleDriveService = googleDriveService;
        this.stringHandler = stringHandler;
    }

    @Override
    public <S extends User> S save(S entity) {
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        return userRepository.save(entity);
    }

    @Override
    public <S extends User> S update(S entity) {
        return userRepository.save(entity);
    }

    @Override
    public User findLoggedInUser() {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new UserAuthenticationException("User unauthorized. Please log in again.");
        }
        String email = String.valueOf(auth.getPrincipal());
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else
            throw new RuntimeException("User not found");
    }

    @Override
    public UserResponseModel changePassword(PasswordRequestModel passwordRequestModel) {
        User user = findLoggedInUser();

        if (passwordEncoder.matches(passwordRequestModel.getOldPassword(), user.getPassword())) {
            if (passwordRequestModel.getNewPassword().equals(passwordRequestModel.getConfirmPassword())) {
                user.setPassword(passwordRequestModel.getNewPassword());
                user = save(user);
                UserResponseModel userResponseModel = modelMapper.map(user, UserResponseModel.class);
                return userResponseModel;
            } else {
                throw new RuntimeException("Passwords not matched");
            }
        } else {
            throw new RuntimeException("Password incorrect");
        }
    }

    @Override
    public void resetPassword(PasswordResetRequestModel passwordResetRequestModel) {
        User user = userRepository.findByEmail(passwordResetRequestModel.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isDisabled())
            throw new RuntimeException("Account disabled");

        if (passwordResetRequestModel.getNewPassword().equals(passwordResetRequestModel.getConfirmPassword())) {
            user.setPassword(passwordResetRequestModel.getNewPassword());
            save(user);
        } else {
            throw new RuntimeException("Passwords not matched");
        }

    }

    @Override
    public Page<UserResponseModel> getAllUsers(int page, int size, String disabled, String gender, String organization, String role, String s) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Organization foundOrganization = findOrganization(organization);

        Role foundRole = findRole(role);

        Boolean isDisabled = parseDisabled(disabled);

        Integer userGender = parseGender(gender);

        String roleName = "";

        Page<User> users = userRepository.findUsers(isDisabled, userGender, foundOrganization, foundRole, roleName, s, pageable);

        Page<UserResponseModel> userModels = users.map(this::convertToUserModel);
        ;
        return userModels;

    }

    @Override
    public Page<UserResponseModel> getAllUsersByOrganization(String slug, int page, int size, String disabled, String gender, String role, String s) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Organization foundOrganization = findOrganization(slug);

        Role foundRole = findRole(role);

        Boolean isDeleted = parseDisabled(disabled);

        Integer userGender = parseGender(gender);

        User currentUser = findLoggedInUser();

        String roleName = currentUser.getRole().getRoleName().equals("ROLE_MANAGER") ? "ROLE_MANAGER" : "";

        Page<User> users = userRepository.findUsers(isDeleted, userGender, foundOrganization, foundRole, roleName, s, pageable);

        Page<UserResponseModel> userModels = users.map(this::convertToUserModel);
        ;
        return userModels;
    }

    @Override
    public UserResponseModel getUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found"));
        UserResponseModel userResponseModel = convertToDetailUserModel(user);

        return userResponseModel;
    }

    @Override
    public UserResponseModel createUser(MultipartFile file, UserRequestModel userRequestModel) {
        Optional<User> user = userRepository.findByEmail(userRequestModel.getEmail());
        if (user.isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        User newUser = modelMapper.map(userRequestModel, User.class);

        if (userRequestModel.getOrgId() != null && userRequestModel.getOrgId().toString() != "") {
            Organization organization = organizationRepository.findById(userRequestModel.getOrgId()).orElseThrow(
                    () -> new ModelNotFoundException("Organization not found"));
            newUser.setOrganization(organization);
        }
        Role role = roleRepository.findById(userRequestModel.getRoleId()).orElseThrow(
                () -> new ModelNotFoundException("Role not found"));
        newUser.setRole(role);

        if (!userRequestModel.getPassword().equals(userRequestModel.getConfirmPassword())) {
            throw new RuntimeException("Password not match");
        }

        FileModel gd = googleDriveService.uploadImage(file, stringHandler.getEmailUsername(newUser.getEmail()), null, "avatar");
        newUser.setImage(gd.getViewUrl());

        newUser = save(newUser);

        UserResponseModel userResponseModel = modelMapper.map(newUser, UserResponseModel.class);

        return userResponseModel;
    }

    @Override
    public UserResponseModel updateUser(UUID userId, MultipartFile file, UserRequestModel userRequestModel) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found"));

        Optional<User> tempUser = userRepository.findByEmail(userRequestModel.getEmail());
        if (tempUser.isPresent())
            if (tempUser.get().getUserId() != user.getUserId())
                throw new RuntimeException("Email already registered");

        user.setFirstName(userRequestModel.getFirstName());
        user.setLastName(userRequestModel.getLastName());
        user.setDateOfBirth(userRequestModel.getDateOfBirth());
        user.setGender(userRequestModel.getGender());
        user.setEmail(userRequestModel.getEmail());

        if (userRequestModel.getOrgId().toString() != "") {
            Organization organization = organizationRepository.findById(userRequestModel.getOrgId()).orElseThrow(
                    () -> new ModelNotFoundException("Organization not found"));
            user.setOrganization(organization);
        }
        Role role = roleRepository.findById(userRequestModel.getRoleId()).orElseThrow(
                () -> new ModelNotFoundException("Role not found"));
        user.setRole(role);

        if (file != null) {
            if (user.getImage() != null) {
                FileModel gd = googleDriveService.uploadImage(file, stringHandler.getEmailUsername(user.getEmail()), stringHandler.getFileId(user.getImage()), "avatar");
                user.setImage(gd.getViewUrl());
            } else {
                FileModel gd = googleDriveService.uploadImage(file, stringHandler.getEmailUsername(user.getEmail()), null, "avatar");
                user.setImage(gd.getViewUrl());
            }
        }

        // Password changed
        if (userRequestModel.getPassword().trim().equals("")) {
            user = update(user);
        } else {
            if (!userRequestModel.getPassword().equals(userRequestModel.getConfirmPassword())) {
                throw new RuntimeException("Passwords not match");
            } else {
                user.setPassword(userRequestModel.getPassword());
                user = save(user);
            }
        }

        UserResponseModel userResponseModel = modelMapper.map(user, UserResponseModel.class);

        return userResponseModel;
    }

    @Override
    public void disableUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        user.setDisabled(true);
        update(user);

    }

    @Override
    public void enableUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        user.setDisabled(false);
        update(user);
    }

    @Override
    public UserResponseModel getProfile() {
        User user = findLoggedInUser();
        UserResponseModel userResponseModel = convertToDetailUserModel(user);

        return userResponseModel;
    }

    @Override
    public UserResponseModel updateProfile(UserProfileRequestModel userProfileRequestModel) {
        User user = findLoggedInUser();

        Optional<User> tempUser = userRepository.findByEmail(userProfileRequestModel.getEmail());
        if (tempUser.isPresent())
            if (tempUser.get().getUserId() != user.getUserId())
                throw new RuntimeException("Email already registered");

        user.setFirstName(userProfileRequestModel.getFirstName());
        user.setLastName(userProfileRequestModel.getLastName());
        user.setDateOfBirth(userProfileRequestModel.getDateOfBirth());
        user.setGender(userProfileRequestModel.getGender());
        user.setEmail(userProfileRequestModel.getEmail());

        user = update(user);

        UserResponseModel userResponseModel = modelMapper.map(user, UserResponseModel.class);
        return userResponseModel;
    }

    @Override
    public UserResponseModel updateAvatar(MultipartFile file) {
        User user = findLoggedInUser();

        if (user.getImage() != null) {
            FileModel gd = googleDriveService.uploadImage(file, stringHandler.getEmailUsername(user.getEmail()), stringHandler.getFileId(user.getImage()), "avatar");
            user.setImage(gd.getViewUrl());
        } else {
            FileModel gd = googleDriveService.uploadImage(file, stringHandler.getEmailUsername(user.getEmail()), null, "avatar");
            user.setImage(gd.getViewUrl());
        }

        user = update(user);
        UserResponseModel userResponseModel = modelMapper.map(user, UserResponseModel.class);

        return userResponseModel;
    }

    @Override
    public Page<UserResponseModel> getLatestUsers(int page, int size, String disabled, String gender, String organization, String role, String s) {
        Pageable pageable = PageRequest.of(page, size);

        Organization foundOrganization = findOrganization(organization);

        Role foundRole = findRole(role);

        Boolean isDisabled = parseDisabled(disabled);

        Integer userGender = parseGender(gender);

        String roleName = "";

        Page<User> users = userRepository.findLatestUsers(isDisabled, userGender, foundOrganization, foundRole, roleName, s, pageable);

        Page<UserResponseModel> userModels = users.map(this::convertToUserModel);
        ;
        return userModels;

    }

    @Override
    public Page<UserResponseModel> getLatestUsersByOrganization(String slug, int page, int size, String disabled, String gender, String role, String s) {
        Pageable pageable = PageRequest.of(page, size);

        Organization foundOrganization = findOrganization(slug);

        Role foundRole = findRole(role);

        Boolean isDisabled = parseDisabled(disabled);

        Integer userGender = parseGender(gender);

        User currentUser = findLoggedInUser();

        String roleName = currentUser.getRole().getRoleName().equals("ROLE_MANAGER") ? "ROLE_MANAGER" : "";

        Page<User> users = userRepository.findLatestUsers(isDisabled, userGender, foundOrganization, foundRole, roleName, s, pageable);

        Page<UserResponseModel> userModels = users.map(this::convertToUserModel);
        ;
        return userModels;

    }


    public Organization findOrganization(String organization) {
        return organization.equals("all") ?
                null : organizationRepository.findBySlug(organization).orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    public Role findRole(String role) {
        return role.equals("all") ?
                null : roleRepository.findByRoleName(role).orElseThrow(() -> new RuntimeException("Role not found"));
    }

    public Boolean parseDisabled(String disabled) {
        return disabled.equals("all") ?
                null : Boolean.valueOf(disabled);
    }

    public Integer parseGender(String gender) {
        return gender.equals("all") ?
                null : Integer.valueOf(gender);
    }

    private UserResponseModel convertToUserModel(Object o) {
        UserResponseModel userResponseModel = modelMapper.map(o, UserResponseModel.class);
        return userResponseModel;
    }

    private UserResponseModel convertToDetailUserModel(User user) {
        UserResponseModel userResponseModel = modelMapper.map(user, UserResponseModel.class);

        int totalDocuments = user.getUploadedDocuments().size();
        int totalViews = (int) user.getUploadedDocuments().stream()
                .mapToLong(Document::getTotalView)
                .sum();
        int totalLikes = (int) user.getUploadedDocuments().stream()
                .flatMap(document -> document.getDocumentLikes().stream())
                .count();
        int totalPosts = user.getPosts().size();
        int totalReplies = user.getReplies().size();
        int totalPostLikes = user.getPosts().stream()
                .flatMapToInt(post -> IntStream.of(post.getPostLikes().size()))
                .sum();
        BadgeLeanModel badge = badgeService.findBestBadge(user.getUserId());

        userResponseModel.setTotalDocuments(totalDocuments);
        userResponseModel.setTotalViews(totalViews);
        userResponseModel.setTotalLikes(totalLikes);
        userResponseModel.setTotalPosts(totalPosts);
        userResponseModel.setTotalReplies(totalReplies);
        userResponseModel.setTotalPostLikes(totalPostLikes);
        userResponseModel.setBadge(badge);

        return userResponseModel;
    }
}
