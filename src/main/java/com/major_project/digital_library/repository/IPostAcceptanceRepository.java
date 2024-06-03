package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.PostAcceptance;
import com.major_project.digital_library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IPostAcceptanceRepository extends JpaRepository<PostAcceptance, UUID> {
    Optional<PostAcceptance> findByPostAndUser(Post post, User user);
}
