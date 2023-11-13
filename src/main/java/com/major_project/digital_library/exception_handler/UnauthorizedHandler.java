package com.major_project.digital_library.exception_handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.major_project.digital_library.model.response_model.ResponseModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class UnauthorizedHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(OK.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(),
                ResponseModel.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error(true)
                        .message("User unauthorized. Please log in first.")
                        //.message(authException.getMessage())
                        .build());
    }
}
