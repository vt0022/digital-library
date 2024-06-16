package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.ReplyRequestModel;
import com.major_project.digital_library.model.response_model.ReplyResponseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface IReplyService {
    Page<Reply> findAllByPostOrderByCreatedAtAsc(Post post, Pageable pageable);

    <S extends Reply> S save(S entity);

    Optional<Reply> findById(UUID uuid);

    Page<Reply> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    void deleteById(UUID uuid);

    Page<ReplyResponseModel> getRepliesOfPostForGuest(UUID postId, int page, int size);

    Page<ReplyResponseModel> getAllRepliesOfPost(UUID postId, int page, int size);

    Page<ReplyResponseModel> getViewableRepliesOfPost(UUID postId, int page, int size);

    ReplyResponseModel addReply(UUID postId, ReplyRequestModel replyRequestModel);

    ReplyResponseModel editReply(UUID replyId, Map<String, String> replyContent);

    boolean deleteReply(UUID replyId);

    Page<ReplyResponseModel> getViewableRepliesOfUser(UUID userId, int page, int size);

    Page<ReplyResponseModel> getAllRepliesOfUser(UUID userId, int page, int size);

    ReplyResponseModel convertToReplyModelForGuest(Reply reply);

    ReplyResponseModel convertToReplyModel(Reply reply);
}
