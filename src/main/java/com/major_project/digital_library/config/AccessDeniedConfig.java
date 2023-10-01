package com.major_project.digital_library.config;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.major_project.digital_library.model.response_model.ShortResponseModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class AccessDeniedConfig implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(OK.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(),
                ShortResponseModel.builder()
                        .status(403)
                        .error(true)
                        .message("Permission needed to access this")
                        .build());
    }
}

