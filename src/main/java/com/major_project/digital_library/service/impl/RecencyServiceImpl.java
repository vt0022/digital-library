package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Recency;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.repository.IRecencyRepository;
import com.major_project.digital_library.service.IRecencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RecencyServiceImpl implements IRecencyService {
    private final IRecencyRepository recencyRepository;

    @Autowired
    public RecencyServiceImpl(IRecencyRepository recencyRepository) {
        this.recencyRepository = recencyRepository;
    }

    @Override
    public Optional<Recency> findByUserAndDocument(User user, Document document) {
        return recencyRepository.findByUserAndDocument(user, document);
    }

    @Transactional
    @Override
    public void deleteByUserAndDocument(User user, Document document) {
        recencyRepository.deleteByUserAndDocument(user, document);
    }

    @Override
    public List<Recency> findByUserOrderByAccessedAtDesc(User user) {
        return recencyRepository.findByUserOrderByAccessedAtDesc(user);
    }

    @Override
    public <S extends Recency> S save(S entity) {
        return recencyRepository.save(entity);
    }
}
