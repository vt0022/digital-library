package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.PostLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.entity.UserPostKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPostLikeRepository extends JpaRepository<PostLike, UserPostKey> {
    boolean existsByUserAndPost(User user, Post post);

    Optional<PostLike> findByUserAndPost(User user, Post post);

    void deleteByUserAndPost(User user, Post post);

    @Query("SELECT p FROM PostLike p " +
            "WHERE p.user = :user " +
            "AND p.post.isDisabled = FALSE " +
            "AND (p.post.label.isDisabled = FALSE OR p.post.label IS NULL) " +
            "AND p.post.subsection.isDisabled = FALSE " +
            "AND p.post.subsection.section.isDisabled = FALSE " +
            "ORDER BY p.likedAt DESC")
    Page<PostLike> findAllByUser(User user, Pageable pageable);
}
