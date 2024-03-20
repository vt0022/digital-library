package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.DocumentLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.repository.IDocumentLikeRepository;
import com.major_project.digital_library.service.IDocumentLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DocumentLikeServiceImpl implements IDocumentLikeService {
    private final IDocumentLikeRepository documentLikeRepository;

    @Autowired
    public DocumentLikeServiceImpl(IDocumentLikeRepository documentLikeRepository) {
        this.documentLikeRepository = documentLikeRepository;
    }

    @Override
    public <S extends DocumentLike> S save(S entity) {
        return documentLikeRepository.save(entity);
    }

    @Override
    public void delete(DocumentLike entity) {
        documentLikeRepository.delete(entity);
    }

    @Override
    public boolean existsByUserAndDocument(User user, Document document) {
        return documentLikeRepository.existsByUserAndDocument(user, document);
    }

    @Override
    public Optional<DocumentLike> findByUserAndDocument(User user, Document document) {
        return documentLikeRepository.findByUserAndDocument(user, document);
    }

    @Override
    public Page<DocumentLike> findByUser(User user, Pageable pageable) {
        return documentLikeRepository.findByUser(user, pageable);
    }


}
