package com.major_project.digital_library.exception_handler;

import com.major_project.digital_library.model.response_model.ResponseModel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
                .status(HttpStatus.BAD_REQUEST.value())
                .error(true)
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialException(Exception e) {
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(true)
                .message("Invalid password")
                .build());
    }
    //SignatureVerificationException
    //TokenExpiredException
    //InvalidTokenException

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
}
