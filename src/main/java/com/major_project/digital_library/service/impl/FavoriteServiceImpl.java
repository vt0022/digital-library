package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Favorite;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.repository.IFavoriteRepository;
import com.major_project.digital_library.service.IFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FavoriteServiceImpl implements IFavoriteService {
    private final IFavoriteRepository favoriteRepository;

    @Autowired
    public FavoriteServiceImpl(IFavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public <S extends Favorite> S save(S entity) {
        return favoriteRepository.save(entity);
    }

    @Override
    public boolean existsByUserAndDocument(User user, Document document) {
        return favoriteRepository.existsByUserAndDocument(user, document);
    }

    @Override
    public Optional<Favorite> findByUserAndDocument(User user, Document document) {
        return favoriteRepository.findByUserAndDocument(user, document);
    }

    @Override
    public Page<Favorite> findByUserAndIsLiked(User user, boolean isLiked, Pageable pageable) {
        return favoriteRepository.findByUserAndIsLiked(user, isLiked, pageable);
    }


}
