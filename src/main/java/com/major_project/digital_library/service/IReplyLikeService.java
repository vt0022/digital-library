package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.response_model.ReplyLikeResponseModel;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface IReplyLikeService {
    boolean existsByUserAndReply(User user, Reply reply);

    <S extends ReplyLike> S save(S entity);

    Optional<ReplyLike> findByUserAndReply(User user, Reply reply);

    void delete(ReplyLike entity);

    boolean likeReply(UUID replyId);

    Page<ReplyLikeResponseModel> findByUser(int page, int size);
}
