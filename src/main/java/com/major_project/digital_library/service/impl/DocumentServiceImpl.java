package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentServiceImpl {
    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public <S extends Document> S save(S entity) {
        return documentRepository.save(entity);
    }

    public Optional<Document> findById(UUID uuid) {
        return documentRepository.findById(uuid);
    }

    public void deleteById(UUID uuid) {
        documentRepository.deleteById(uuid);
    }

    public List<Document> findAll(Sort sort) {
        return documentRepository.findAll(sort);
    }

    public Page<Document> findAll(Pageable pageable) {
        return documentRepository.findAll(pageable);
    }

}
