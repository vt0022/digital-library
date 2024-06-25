package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.DocumentNote;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.entity.UserDocumentPageKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IDocumentNoteRepository extends JpaRepository<DocumentNote, UserDocumentPageKey> {
    Optional<DocumentNote> findByUserAndDocumentAndPage(User user, Document document, int page);

    Page<DocumentNote> findByUserAndDocument(User user, Document document, Pageable pageable);
}
