package com.major_project.digital_library.exception_handler;

import com.major_project.digital_library.model.response_model.ResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception e) {
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(400)
                .error(true)
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialException(Exception e) {
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(400)
                .error(true)
                .message("Invalid password")
                .build());
    }
    //SignatureVerificationException
    //TokenExpiredException
    //InvalidTokenException
}
