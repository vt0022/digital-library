package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Favorite;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.entity.UserDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IFavoriteRepository extends JpaRepository<Favorite, UserDocument> {
    boolean existsByUserAndDocument(User user, Document document);

    Optional<Favorite> findByUserAndDocument(User user, Document document);

    Page<Favorite> findByUserAndIsLiked(User user, boolean isLiked, Pageable pageable);
}
