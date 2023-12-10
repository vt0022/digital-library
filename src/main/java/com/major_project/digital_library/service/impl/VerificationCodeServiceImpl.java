package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.entity.VerificationCode;
import com.major_project.digital_library.repository.IVerificationCodeRepository;
import com.major_project.digital_library.service.IVerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationCodeServiceImpl implements IVerificationCodeService {
    private final IVerificationCodeRepository verificationCodeRepository;

    @Autowired
    public VerificationCodeServiceImpl(IVerificationCodeRepository verificationCodeRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
    }

    @Override
    public Optional<VerificationCode> findByUser(User user) {
        return verificationCodeRepository.findByUser(user);
    }

    @Override
    public <S extends VerificationCode> S save(S entity) {
        return verificationCodeRepository.save(entity);
    }
}
