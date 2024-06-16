package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyAcceptance;
import com.major_project.digital_library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IReplyAcceptanceRepository extends JpaRepository<ReplyAcceptance, UUID> {
    Optional<ReplyAcceptance> findByReplyAndUser(Reply reply, User user);

    @Query("SELECT ra FROM ReplyAcceptance ra " +
            "WHERE ra.reply.post = :post")
    Optional<ReplyAcceptance> findByPost(Post post);

    @Query("SELECT ra FROM ReplyAcceptance ra " +
            "WHERE ra.reply.post = :post " +
            "AND ra.user = :user")
    Optional<ReplyAcceptance> findByPostAndUser(Post post, User user);
}
