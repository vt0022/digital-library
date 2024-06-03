package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.PostAcceptance;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.repository.IPostAcceptanceRepository;
import com.major_project.digital_library.repository.IPostRepository;
import com.major_project.digital_library.service.IPostAcceptanceService;
import com.major_project.digital_library.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PostAcceptanceServiceImpl implements IPostAcceptanceService {
    private final IPostAcceptanceRepository postAcceptationRepository;
    private final IPostRepository postRepository;
    private final IUserService userService;

    @Autowired
    public PostAcceptanceServiceImpl(IPostAcceptanceRepository postAcceptationRepository, IPostRepository postRepository, IUserService userService) {
        this.postAcceptationRepository = postAcceptationRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Override
    public void doAccept(UUID postId) {
        User user = userService.findLoggedInUser();

        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        PostAcceptance postAcceptance = new PostAcceptance();
        postAcceptance.setPost(post);
        postAcceptance.setUser(user);
        postAcceptationRepository.save(postAcceptance);
    }

    @Override
    public void undoAccept(UUID postId) {
        User user = userService.findLoggedInUser();

        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        PostAcceptance postAcceptance = postAcceptationRepository.findByPostAndUser(post, user).orElseThrow(() -> new RuntimeException("Acceptation not found"));
        postAcceptationRepository.delete(postAcceptance);
    }
}
