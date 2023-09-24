package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentService {
    <S extends Document> S save(S entity);

    Optional<Document> findById(UUID uuid);

    void deleteById(UUID uuid);

    List<Document> findAll(Sort sort);

    Page<Document> findAll(Pageable pageable);

    Optional<Document> findBySlug(String slug);

    List<Document> findAll();
}
