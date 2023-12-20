package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.Review;
import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IReviewService {
    <S extends Review> S save(S entity);

    Page<Review> findByDocumentOrganization(Organization organization, Pageable pageable);

    Optional<Review> findById(UUID uuid);

    void deleteById(UUID uuid);

    boolean existsByUserAndDocument(User user, Document document);
}
