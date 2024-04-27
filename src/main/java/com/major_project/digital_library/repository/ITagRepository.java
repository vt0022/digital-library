package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ITagRepository extends JpaRepository<Tag, UUID> {
    Optional<Tag> findByTagName(String tagName);

    boolean existsByTagName(String tagName);
}
