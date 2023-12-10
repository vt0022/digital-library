package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.entity.VerificationCode;

import java.util.Optional;

public interface IVerificationCodeService {
    Optional<VerificationCode> findByUser(User user);

    <S extends VerificationCode> S save(S entity);
}
