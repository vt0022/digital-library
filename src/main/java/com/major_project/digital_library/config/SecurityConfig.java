package com.major_project.digital_library.config;

import com.major_project.digital_library.exception_handler.AccessDeniedHandler;
import com.major_project.digital_library.exception_handler.UnauthorizedHandler;
import com.major_project.digital_library.filter.JWTAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Autowired
    public SecurityConfig(JWTAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public AccessDeniedHandler accessDenied() {
        return new AccessDeniedHandler();
    }

    @Bean
    public UnauthorizedHandler unauthorized() {
        return new UnauthorizedHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Xem xét lại nếu bị lỗi 403

                .anonymous(anonymous -> anonymous.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui", "/swagger-ui/**").permitAll()
                        .requestMatchers("/api-docs", "/api-docs/**", "/api/v1/document/test").permitAll()
                        .requestMatchers("/api/v1/public/**").permitAll()
//                        .requestMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")
//                        .requestMatchers("/api/v1/manager/**").hasAuthority("ROLE_MANAGER")
//                        .requestMatchers("/api/v1/student/**").hasAuthority("ROLE_STUDENT")
//                        .requestMatchers("/api/v1/lecturer/**").hasAuthority("ROLE_LECTURER")
                        .anyRequest().permitAll())

                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .formLogin(login -> login
                        .permitAll())// Nếu không có dòng này thì sẽ không hiển thị được trang login mặc định mà chỉ popup

                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll())

                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDenied())
                        .authenticationEntryPoint(unauthorized())
                )

                .httpBasic(Customizer.withDefaults())
        ;
        return http.build();
    }
}
