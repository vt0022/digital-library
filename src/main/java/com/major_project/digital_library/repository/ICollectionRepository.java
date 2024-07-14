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

    Optional<Collection> findByUserAndCollectionName(User user, String collectionName);

    @Query("SELECT c FROM Collection c " +
            "WHERE c.isPrivate = FALSE " +
            "AND LOWER(c.collectionName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Collection> findPublicCollections(String query, Pageable pageable);

    @Query("SELECT c FROM Collection c " +
            "WHERE c.user = :user " +
            "AND LOWER(c.collectionName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Collection> findByUser(User user, String query, Pageable pageable);

    @Query("SELECT c FROM Collection c " +
            "WHERE (c.isPrivate = FALSE OR c.user = :user) " +
            "AND LOWER(c.collectionName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Collection> findForUser(User user, String query, Pageable pageable);
}
