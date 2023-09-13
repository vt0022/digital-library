package com.major_project.digital_library.service;

import com.major_project.digital_library.auth.AuthenticationRequest;
import com.major_project.digital_library.auth.AuthenticationResponse;
import com.major_project.digital_library.entity.Role;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.repository.UserRepositoty;
import com.major_project.digital_library.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepositoty userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        Role role = new Role();
        if (user != null) {
            role = user.getRole();
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        // Generate JWT
        var jwtToken = jwtService.generateToken(user, authorities);
        var jwtRefreshToken = jwtService.generateRefreshToken(user, authorities);

        return AuthenticationResponse.builder().token(jwtToken).refreshToken(jwtRefreshToken).build();
    }

}
