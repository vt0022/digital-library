package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Save;
import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ISaveService {
    <S extends Save> S save(S entity);

    boolean existsByUserAndDocument(User user, Document document);

    Optional<Save> findByUserAndDocument(User user, Document document);

    Page<Save> findByUserAndIsSaved(User user, boolean isSaved, Pageable pageable);
}
