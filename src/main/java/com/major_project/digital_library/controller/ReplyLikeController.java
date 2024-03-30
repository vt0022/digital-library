package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IReplyLikeService;
import com.major_project.digital_library.service.IReplyService;
import com.major_project.digital_library.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ReplyLikeController {
    private final IReplyLikeService replyLikeService;
    private final IUserService userService;
    private final IReplyService replyService;

    @Autowired
    public ReplyLikeController(IUserService userService, IReplyLikeService replyLikeService, IReplyService replyService) {
        this.replyLikeService = replyLikeService;
        this.replyService = replyService;
        this.userService = userService;
    }

    @PostMapping("/replies/{replyId}/like")
    public ResponseEntity<?> likeReply(@PathVariable UUID replyId) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));

        Reply reply = replyService.findById(replyId).orElseThrow(() -> new RuntimeException("Reply not found"));

        Optional<ReplyLike> replyLike = replyLikeService.findByUserAndReply(user, reply);

        if (replyLike.isPresent()) {
            replyLikeService.delete(replyLike.get());
        } else {
            ReplyLike newReplyLike = new ReplyLike();
            newReplyLike.setUser(user);
            newReplyLike.setReply(reply);
            replyLikeService.save(newReplyLike);
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message((replyLike.isPresent() ? "Unlike " : "Like ") + "reply successfully")
                .build());
    }
}
