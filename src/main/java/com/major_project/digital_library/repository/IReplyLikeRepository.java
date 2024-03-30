package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.entity.UserReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IReplyLikeRepository extends JpaRepository<ReplyLike, UserReply> {
    boolean existsByUserAndReply(User user, Reply reply);

    Optional<ReplyLike> findByUserAndReply(User user, Reply reply);
}
