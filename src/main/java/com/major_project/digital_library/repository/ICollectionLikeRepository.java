package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Collection;
import com.major_project.digital_library.entity.CollectionLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.entity.UserCollectionKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICollectionLikeRepository extends JpaRepository<CollectionLike, UserCollectionKey> {
    Optional<CollectionLike> findByUserAndCollection(User user, Collection collection);

    @Query("SELECT c FROM Collection c " +
            "JOIN CollectionLike l " +
            "ON c = l.collection " +
            "WHERE l.user = :user " +
            "AND LOWER(c.collectionName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "ORDER BY l.likedAt DESC"
    )
    Page<Collection> findLikedCollections(User user, String query, Pageable pageable);

    ;
}
