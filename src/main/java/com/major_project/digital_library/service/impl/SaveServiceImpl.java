package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Save;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.repository.ISaveRepository;
import com.major_project.digital_library.service.ISaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SaveServiceImpl implements ISaveService {
    private final ISaveRepository saveRepository;

    @Autowired
    public SaveServiceImpl(ISaveRepository saveRepository) {
        this.saveRepository = saveRepository;
    }

    @Override
    public <S extends Save> S save(S entity) {
        return saveRepository.save(entity);
    }

    @Override
    public boolean existsByUserAndDocument(User user, Document document) {
        return saveRepository.existsByUserAndDocument(user, document);
    }

    @Override
    public Optional<Save> findByUserAndDocument(User user, Document document) {
        return saveRepository.findByUserAndDocument(user, document);
    }

    @Override
    public Page<Save> findByUserAndIsSaved(User user, boolean isSaved, Pageable pageable) {
        return saveRepository.findByUserAndIsSaved(user, isSaved, pageable);
    }
}
