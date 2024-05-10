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
//
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
                                "/api/v2/swagger-ui",
                                "/api/v2/swagger-ui/**",
                                "/api/v2/api-docs",
                                "/api/v2/api-docs/**").permitAll()

                        .requestMatchers(
                                "/api/v2/auth/*").permitAll()

                        .requestMatchers(
                                "/api/v2/init/**",
                                "/api/v2/users/pass/all").permitAll()

                        .requestMatchers(
                                "/api/v2/categories/search").permitAll()
                        .requestMatchers(
                                "/api/v2/categories/all").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v2/categories/*/activation").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v2/categories/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v2/categories/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v2/categories/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v2/categories").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v2/categories").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                "/api/v2/fields/search").permitAll()
                        .requestMatchers(
                                "/api/v2/fields/all").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v2/fields/*/activation").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v2/fields/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v2/fields/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v2/fields/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v2/fields").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v2/fields").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                "/api/v2/organizations/search").permitAll()
                        .requestMatchers(
                                "/api/v2/organizations/all").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v2/organizations/*/activation").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v2/organizations/*/reviews").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v2/organizations/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v2/organizations/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v2/organizations/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v2/organizations").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v2/organizations").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                "/api/v2/documents/public/search",
                                "/api/v2/documents/related/*",
                                "/api/v2/documents/public").permitAll()
                        .requestMatchers(
                                "/api/v2/documents/*/like",
                                "/api/v2/documents/*/unlike",
                                "/api/v2/documents/*/relike",
                                "/api/v2/documents/*/review",
                                "/api/v2/documents/*/save",
                                "/api/v2/documents/*/unsave",
                                "/api/v2/documents/*/resave",
                                "/api/v2/documents/*/recent",
                                "/api/v2/documents/saved",
                                "/api/v2/documents/liked",
                                "/api/v2/documents/recent",
                                "/api/v2/documents/students/search",
                                "/api/v2/documents/students",
                                "/api/v2/documents/myuploads").hasAuthority("ROLE_STUDENT")
                        .requestMatchers(
                                "/api/v2/documents/mine").authenticated()
                        .requestMatchers(
                                "/api/v2/documents/latest",
                                "/api/v2/documents/cateFalse",
                                "/api/v2/documents/search").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(
                                "/api/v2/documents/organizations/*/latest",
                                "/api/v2/documents/organizations/*/search").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(
                                "/api/v2/documents/*/reviews/count",
                                "/api/v2/documents/*/reviews",
                                "/api/v2/documents/view/user/*/public").permitAll()
                        .requestMatchers(
                                "/api/v2/documents/view/user/*").hasAuthority("ROLE_STUDENT")
                        .requestMatchers(
                                "/api/v2/documents/*/approval",
                                "/api/v2/documents/user/*",
                                "/api/v2/documents/pending").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(
                                "/api/v2/documents/organizations/*").hasAuthority("ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v2/documents/*/public").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v2/documents/*").authenticated()
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v2/documents/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v2/documents/*").authenticated()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v2/documents").authenticated()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v2/documents").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                "/api/v2/users/search").permitAll()
                        .requestMatchers(
                                "/api/v2/users/password/reset").permitAll()
                        .requestMatchers(
                                "/api/v2/users/profile",
                                "/api/v2/users/password",
                                "/api/v2/users/avatar").authenticated()
                        .requestMatchers(
                                "/api/v2/user/organizations/*/latest",
                                "/api/v2/user/organizations/*").hasAuthority("ROLE_MANAGER")

                        .requestMatchers(
                                "/api/v2/user/latest").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/api/v2/users/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v2/users/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v2/users/*").permitAll()
                        .requestMatchers(
                                "/api/v2/users").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                "/api/v2/statistics/admin").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(
                                "/api/v2/statistics/manager").hasAuthority("ROLE_MANAGER")

                        .requestMatchers(
                                "/api/v2/reviews/*/approval").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(
                                "/api/v2/reviews/mine").hasAuthority("ROLE_STUDENT")
                        .requestMatchers(
                                "/api/v2/reviews/*").authenticated()

                        .requestMatchers(
                                "/api/v2/posts/user/*",
                                "/api/v2/posts/*/replies/guest",
                                "/api/v2/posts/*/guest",
                                "/api/v2/posts/*/history").permitAll()
                        .requestMatchers(
                                "/api/v2/posts/*/replies",
                                "/api/v2/posts/*/reply",
                                "/api/v2/posts/*/like").hasAuthority("ROLE_STUDENT")
                        .requestMatchers(
                                "/api/v2/posts/related").permitAll()
                        .requestMatchers(
                                "/api/v2/posts/*").hasAuthority("ROLE_STUDENT")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v2/posts").hasAuthority("ROLE_STUDENT")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v2/posts").permitAll()

                        .requestMatchers(
                                "/api/v2/replies/image",
                                "/api/v2/replies/user/*",
                                "/api/v2/replies/*/history").permitAll()
                        .requestMatchers(
                                "/api/v2/replies/*").hasAuthority("ROLE_STUDENT")

                        .requestMatchers(
                                "/api/v2/sections/active").permitAll()

                        .requestMatchers(
                                "/api/v2/sections/editable").hasAuthority("ROLE_STUDENT")

                        .requestMatchers(
                                "/api/v2/labels/active").permitAll()

                        .requestMatchers(
                                "/api/v2/badges/user/*").permitAll()

                        .requestMatchers("/api/v2/collections/*/public",
                                "/api/v2/collections/public"
                        ).permitAll()
                        .requestMatchers(
                                "/api/v2/collections/*/document/*",
                                "/api/v2/collections/mine",
                                "/api/v2/collections/*",
                                "/api/v2/collections").authenticated()

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
