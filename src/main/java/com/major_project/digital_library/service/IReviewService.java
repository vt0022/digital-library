package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Review;

import java.util.UUID;

public interface IReviewService {
    <S extends Review> S save(S entity);

    void deleteById(UUID uuid);
}
