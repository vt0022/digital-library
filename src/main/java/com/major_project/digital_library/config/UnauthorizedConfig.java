package com.major_project.digital_library.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.major_project.digital_library.model.response_model.ShortResponseModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class UnauthorizedConfig implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(OK.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(),
                ShortResponseModel.builder()
                        .status(401)
                        .error(true)
                        .message("User unauthorized")
                        .build());
    }
}
