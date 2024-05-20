package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.entity.UserReplyKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IReplyLikeRepository extends JpaRepository<ReplyLike, UserReplyKey> {
    boolean existsByUserAndReply(User user, Reply reply);

    Optional<ReplyLike> findByUserAndReply(User user, Reply reply);

    @Query("SELECT r FROM ReplyLike r " +
            "WHERE r.user = :user " +
            "AND r.reply.isDisabled = FALSE " +
            "AND r.reply.post.isDisabled = FALSE " +
            "AND (r.reply.post.label.isDisabled = FALSE OR r.reply.post.label IS NULL) " +
            "AND r.reply.post.subsection.isDisabled = FALSE " +
            "AND r.reply.post.subsection.section.isDisabled = FALSE " +
            "ORDER BY r.likedAt DESC")
    Page<ReplyLike> findAllByUser(User user, Pageable pageable);
}
