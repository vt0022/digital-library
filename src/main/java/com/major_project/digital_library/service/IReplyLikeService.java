package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyLike;
import com.major_project.digital_library.entity.User;

import java.util.Optional;

public interface IReplyLikeService {
    boolean existsByUserAndReply(User user, Reply reply);

    <S extends ReplyLike> S save(S entity);

    Optional<ReplyLike> findByUserAndReply(User user, Reply reply);

    void delete(ReplyLike entity);
}
