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
//                    cs.setAllowedOrigins(List.of(request.getHeader("Origin")));
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
                                "/api/v1/init/*",
                                "/api/v1/users/pass/all").permitAll()

                        .requestMatchers(
                                "/api/v1/categories/all").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/categories/*/activation").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/categories/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/categories/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/categories/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/categories").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/categories").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                "/api/v1/fields/all").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/fields/*/activation").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/fields/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/fields/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/fields/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/fields").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/fields").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                "/api/v1/organizations/search").permitAll()
                        .requestMatchers(
                                "/api/v1/organizations/all").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/organizations/*/activation").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/organizations/*/reviews").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/organizations/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/organizations/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/organizations/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/organizations").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/organizations").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                "/api/v1/documents/public/search",
                                "/api/v1/documents/public").permitAll()
                        .requestMatchers(
                                "/api/v1/documents/*/liked",
                                "/api/v1/documents/*/like",
                                "/api/v1/documents/*/reviewed",
                                "/api/v1/documents/*/review",
                                "/api/v1/documents/*/saved",
                                "/api/v1/documents/*/save",
                                "/api/v1/documents/saved",
                                "/api/v1/documents/liked",
                                "/api/v1/documents/students/search",
                                "/api/v1/documents/students",
                                "/api/v1/documents/myuploads").hasAuthority("ROLE_STUDENT")
                        .requestMatchers(
                                "/api/v1/documents/mine").authenticated()
                        .requestMatchers(
                                "/api/v1/documents/latest",
                                "/api/v1/documents/cateFalse",
                                "/api/v1/documents/search").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(
                                "/api/v1/documents/organizations/*/latest",
                                "/api/v1/documents/organizations/*/search").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(
                                "/api/v1/documents/*/reviews",
                                "/api/v1/documents/view/user/*/public").permitAll()
                        .requestMatchers(
                                "/api/v1/documents/view/user/*").hasAuthority("ROLE_STUDENT")
                        .requestMatchers(
                                "/api/v1/documents/*/approval",
                                "/api/v1/documents/user/*",
                                "/api/v1/documents/pending").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(
                                "/api/v1/documents/organizations/*").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/documents/*/public").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/documents/*").authenticated()
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/documents/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/documents/*").authenticated()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/documents").authenticated()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/documents").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                "/api/v1/users/password/reset").permitAll()
                        .requestMatchers(
                                "/api/v1/users/profile",
                                "/api/v1/users/password",
                                "/api/v1/users/avatar").authenticated()
                        .requestMatchers(
                                "/api/v1/user/organizations/*/latest",
                                "/api/v1/user/organizations/*").hasAuthority("ROLE_MANGAER")
                        .requestMatchers(
                                "/api/v1/user/latest").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/users/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/users/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/users/*").permitAll()
                        .requestMatchers(
                                "/api/v1/users").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                "/api/v1/statistics/admin").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(
                                "/api/v1/statistics/manager").hasAuthority("ROLE_MANAGER")

                        .requestMatchers(
                                "/api/v1/reviews/*").hasAuthority("ROLE_MANAGER")

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
