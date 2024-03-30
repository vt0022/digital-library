package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IPostService {
    <S extends Post> S save(S entity);

    Optional<Post> findById(UUID uuid);

    void deleteById(UUID uuid);

    Page<Post> findAll(Pageable pageable);

    Page<Post> findPosts(int page, int size, String order, String query);
}
