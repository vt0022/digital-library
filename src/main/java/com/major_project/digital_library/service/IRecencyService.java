package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Recency;
import com.major_project.digital_library.entity.User;

import java.util.List;
import java.util.Optional;

public interface IRecencyService {
    Optional<Recency> findByUserAndDocument(User user, Document document);

    void deleteByUserAndDocument(User user, Document document);

    List<Recency> findByUserOrderByAccessedAtDesc(User user);

    <S extends Recency> S save(S entity);
}
