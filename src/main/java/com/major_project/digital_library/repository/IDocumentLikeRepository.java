package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.DocumentLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.entity.UserDocumentKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IDocumentLikeRepository extends JpaRepository<DocumentLike, UserDocumentKey> {
    boolean existsByUserAndDocument(User user, Document document);

    Optional<DocumentLike> findByUserAndDocument(User user, Document document);

    Page<DocumentLike> findByUser(User user, Pageable pageable);
}
