package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.DocumentLike;
import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IDocumentLikeService {
    <S extends DocumentLike> S save(S entity);

    void delete(DocumentLike entity);

    boolean existsByUserAndDocument(User user, Document document);

    Optional<DocumentLike> findByUserAndDocument(User user, Document document);

    Page<DocumentLike> findByUser(User user, Pageable pageable);
}
