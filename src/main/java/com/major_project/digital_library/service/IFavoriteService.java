package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Favorite;
import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IFavoriteService {
    <S extends Favorite> S save(S entity);


    boolean existsByUserAndDocument(User user, Document document);

    Optional<Favorite> findByUserAndDocument(User user, Document document);

    Page<Favorite> findByUserAndIsLiked(User user, boolean isLiked, Pageable pageable);
}
