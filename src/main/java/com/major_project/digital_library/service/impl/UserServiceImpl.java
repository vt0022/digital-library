package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.exception_handler.exception.UserAuthenticationException;
import com.major_project.digital_library.repository.IUserRepositoty;
import com.major_project.digital_library.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepositoty userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(IUserRepositoty userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Query("SELECT u FROM User u WHERE MONTH(u.createdAt) = MONTH(CURRENT_DATE) AND YEAR(u.createdAt) = YEAR(CURRENT_DATE) ORDER BY u.createdAt DESC")
    public Page<User> findLatestUsers(Pageable pageable) {
        return userRepository.findLatestUsers(pageable);
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
    public void deleteById(UUID uuid) {
        userRepository.deleteById(uuid);
    }

    @Override
    public Optional<User> findById(UUID uuid) {
        return userRepository.findById(uuid);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByEmailAndIsDeleted(String email, boolean isDeleted) {
        return userRepository.findByEmailAndIsDeleted(email, isDeleted);
    }

    @Override
    public Optional<User> findLoggedInUser() {
        // Find user info
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new UserAuthenticationException("User unauthorized. Please log in again.");
        }
        String email = String.valueOf(auth.getPrincipal());
        return userRepository.findByEmail(email);
    }

    @Override
    @Query("SELECT u FROM User u " +
            "WHERE MONTH(u.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(u.createdAt) = YEAR(CURRENT_DATE) " +
            "AND u.organization = :organization " +
            "AND u.role.roleName <> 'ROLE_MANAGER' " +
            "ORDER BY u.createdAt DESC")
    public Page<User> findLatestUsersByOrganization(Organization organization, Pageable pageable) {
        return userRepository.findLatestUsersByOrganization(organization, pageable);
    }

    @Override
    @Query("SELECT u FROM User u " +
            "WHERE u.organization = :organization " +
            "AND u.role.roleName <> 'ROLE_MANAGER'")
    public Page<User> findByOrganization(Organization organization, Pageable pageable) {
        return userRepository.findByOrganization(organization, pageable);
    }

    @Override
    public long countByOrganization(Organization organization) {
        return userRepository.countByOrganization(organization);
    }

    @Override
    public long count() {
        return userRepository.count();
    }
}
