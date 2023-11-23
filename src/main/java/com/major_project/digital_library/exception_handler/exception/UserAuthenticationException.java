package com.major_project.digital_library.exception_handler.exception;

import org.springframework.security.core.AuthenticationException;

public class UserAuthenticationException extends AuthenticationException {
    public UserAuthenticationException(String msg) {
        super(msg);
    }
}
