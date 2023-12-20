package com.major_project.digital_library.exception_handler;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.major_project.digital_library.exception_handler.exception.ModelNotFoundException;
import com.major_project.digital_library.exception_handler.exception.UserAuthenticationException;
import com.major_project.digital_library.model.response_model.ResponseModel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataException(Exception e) {
        String message = e.getMessage();
        if (e.getMessage().contains("duplicate key value")) {
            message = "Giá trị đã tồn tại";
        }
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(HttpStatus.CONFLICT.value())
                .error(true)
                .message(message)
                .build());
    }

    @ExceptionHandler(ModelNotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(Exception e) {
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(true)
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception e) {
        String message = e.getMessage();
        int statusCode = HttpStatus.BAD_REQUEST.value();

        if (e instanceof TokenExpiredException) {
            message = "Token is expired";
            statusCode = HttpStatus.UNAUTHORIZED.value();
        } else if (e instanceof SignatureVerificationException) {
            message = "Invalid signature";
            statusCode = HttpStatus.UNAUTHORIZED.value();
        } else if (e instanceof BadCredentialsException) {
            message = "Invalid password";
            statusCode = HttpStatus.UNAUTHORIZED.value();
        } else if (e instanceof UserAuthenticationException) {
            message = "User unauthorized. Please log in again";
            statusCode = HttpStatus.UNAUTHORIZED.value();
        }

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(statusCode)
                .error(true)
                .message(message)
                .build());
    }
}
