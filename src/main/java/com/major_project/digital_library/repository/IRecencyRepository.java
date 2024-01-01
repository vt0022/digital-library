package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Recency;
import com.major_project.digital_library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRecencyRepository extends JpaRepository<Recency, UUID> {
    Optional<Recency> findByUserAndDocument(User user, Document document);

    void deleteByUserAndDocument(User user, Document document);

    List<Recency> findByUserOrderByAccessedAtDesc(User user);
}
