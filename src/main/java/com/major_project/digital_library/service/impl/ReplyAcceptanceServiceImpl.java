package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyAcceptance;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.repository.IReplyAcceptanceRepository;
import com.major_project.digital_library.repository.IReplyRepository;
import com.major_project.digital_library.service.IReplyAcceptanceService;
import com.major_project.digital_library.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReplyAcceptanceServiceImpl implements IReplyAcceptanceService {
    private final IReplyAcceptanceRepository replyAcceptationRepository;
    private final IReplyRepository replyRepository;
    private final IUserService userService;

    @Autowired
    public ReplyAcceptanceServiceImpl(IReplyAcceptanceRepository replyAcceptationRepository, IReplyRepository replyRepository, IUserService userService) {
        this.replyAcceptationRepository = replyAcceptationRepository;
        this.replyRepository = replyRepository;
        this.userService = userService;
    }

    @Override
    public void doAccept(UUID replyId) {
        User user = userService.findLoggedInUser();

        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new RuntimeException("Reply not found"));
        ReplyAcceptance replyAcceptance = new ReplyAcceptance();
        replyAcceptance.setUser(user);
        replyAcceptance.setReply(reply);
        replyAcceptationRepository.save(replyAcceptance);
    }

    @Override
    public void undoAccept(UUID replyId) {
        User user = userService.findLoggedInUser();

        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new RuntimeException("Reply not found"));
        ReplyAcceptance replyAcceptance = replyAcceptationRepository.findByReplyAndUser(reply, user).orElseThrow(() -> new RuntimeException("Acceptation not found"));
        replyAcceptationRepository.delete(replyAcceptance);
    }
}
