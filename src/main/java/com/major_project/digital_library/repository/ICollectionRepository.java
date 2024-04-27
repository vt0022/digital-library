package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Collection;
import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICollectionRepository extends JpaRepository<Collection, UUID> {
    Optional<Collection> findBySlug(String slug);

    Page<Collection> findAllByIsPrivate(boolean isPrivate, Pageable pageable);

    Page<Collection> findByUser(User user, Pageable pageable);

    @Query("SELECT c FROM Collection c " +
            "WHERE c.isPrivate = FALSE " +
            "OR c.user = :user")
    Page<Collection> findForUser(User user, Pageable pageable);
}
