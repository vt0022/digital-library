package com.major_project.digital_library.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.major_project.digital_library.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service

public class JWTService {
    @Value("${API_KEY}")
    private String SECRET_KEY;

    @Value("${ACCESS_TOKEN_LIFE}")
    private int expirationTimeAT = 1000 * 60 * 20;

    @Value("${REFRESH_TOKEN_LIFE}")
    private int expirationTimeRT = 1000 * 60 * 60 * 24;

    public String generateToken(User user, Collection<SimpleGrantedAuthority> authorities) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
        return JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeAT))
                .withClaim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public String generateRefreshToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
        return JWT.create()
                .withSubject(user.getEmail() + " " + user.getUserId())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeRT))
                .sign(algorithm);
    }
}
