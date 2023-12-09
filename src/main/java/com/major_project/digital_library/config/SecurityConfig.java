package com.major_project.digital_library.config;

import com.major_project.digital_library.exception_handler.AccessDeniedHandler;
import com.major_project.digital_library.exception_handler.UnauthorizedHandler;
import com.major_project.digital_library.filter.GlobalCorsFilter;
import com.major_project.digital_library.filter.JWTAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final GlobalCorsFilter globalCorsFilter;

    @Autowired
    public SecurityConfig(JWTAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider, GlobalCorsFilter globalCorsFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
        this.globalCorsFilter = globalCorsFilter;
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
    public SecurityContextRepository securityContextRepository() {
        return new NullSecurityContextRepository(); // I use Null Repository since I don't need it for anything except store information in UserDetails
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Xem xét lại nếu bị lỗi 403

//                .cors(cors -> cors.configurationSource(request -> {
//                    final CorsConfiguration cs = new CorsConfiguration();
////                    cs.setAllowedOrigins(List.of(request.getHeader("Origin")));
//                    cs.setAllowCredentials(true);
//                    cs.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "HEAD", "DELETE", "OPTIONS"));
//                    cs.setAllowedHeaders(List.of("Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method", "Access-Control-Request-Headers", "Authorization"));
//                    cs.setExposedHeaders(List.of("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "Authorization"));
//                    return cs;
//                }))

                .anonymous(anonymous -> anonymous.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/swagger-ui",
                                "/api/v1/swagger-ui/**",
                                "/api/v1/api-docs",
                                "/api/v1/api-docs/**").permitAll()

                        .requestMatchers(
                                "/api/v1/auth/*").permitAll()

                        .requestMatchers(
                                "/api/v1/categories/all").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/categories/*/activation").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/categories/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/categories/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/categories").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/categories").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                "/api/v1/documents/public").permitAll()
                        .requestMatchers(
                                "/api/v1/documents/*/like",
                                "/api/v1/documents/*/review",
                                "/api/v1/documents/*/save",
                                "/api/v1/documents/saved",
                                "/api/v1/documents/liked",
                                "/api/v1/documents/mine",
                                "/api/v1/documents/students").hasAuthority("ROLE_STUDENT")
                        .requestMatchers(
                                "/api/v1/documents/*/reviews/*").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(
                                "/api/v1/documents/*/reviews").permitAll() // Chú ý role
                        .requestMatchers(
                                "/api/v1/documents/*/permanent").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(
                                "/api/v1/documents/*/approval",
                                "/api/v1/documents/waiting").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(
                                "/api/v1/documents/organizations/*").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/documents/*").permitAll()
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/documents/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/documents/*").authenticated()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/documents").authenticated()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/documents").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                "/api/v1/users/profile",
                                "/api/v1/users/password",
                                "/api/v1/users/avatar").authenticated()
                        .requestMatchers("/api/v1/users/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers("/api/v1/users").hasAuthority("ROLE_ADMIN")

                        .anyRequest().authenticated())

                .securityContext((securityContext) -> securityContext.securityContextRepository(securityContextRepository())) // Add Security Context Holder Repository
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(globalCorsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDenied())
                        .authenticationEntryPoint(unauthorized())
                )
                .httpBasic(Customizer.withDefaults())
        ;
        return http.build();
    }
}
