package com.major_project.digital_library.model.response_model;

import com.major_project.digital_library.model.lean_model.PostLeanModel;
import com.major_project.digital_library.model.lean_model.UserLeanModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID replyId;

    private String content;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private ReplyResponseModel parentReply;

    private UserLeanModel user;

    private boolean isLiked;

    private int totalLikes;

    private boolean isMy;

    private boolean isDisabled;

    private boolean isAccepted;

    private boolean isPostDisabled;

    private PostLeanModel post;
}
