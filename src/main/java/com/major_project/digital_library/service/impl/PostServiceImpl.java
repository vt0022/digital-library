package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.repository.IPostRepository;
import com.major_project.digital_library.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PostServiceImpl implements IPostService {
    private final IPostRepository postRepository;

    @Autowired
    public PostServiceImpl(IPostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public <S extends Post> S save(S entity) {
        return postRepository.save(entity);
    }

    @Override
    public Optional<Post> findById(UUID uuid) {
        return postRepository.findById(uuid);
    }

    @Override
    public void deleteById(UUID uuid) {
        postRepository.deleteById(uuid);
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public Page<Post> findPosts(int page, int size, String order, String query) {
        if (order.equals("mostViewed")) {
            Sort sort = Sort.by(Sort.Direction.DESC, "totalViews");
            Pageable pageable = PageRequest.of(page, size, sort);
            return postRepository.findAllPosts(query, pageable);
        } else if (order.equals("leastViewed")) {
            Sort sort = Sort.by(Sort.Direction.ASC, "totalViews");
            Pageable pageable = PageRequest.of(page, size, sort);
            return postRepository.findAllPosts(query, pageable);
        } else if (order.equals("mostLiked")) {
            Pageable pageable = PageRequest.of(page, size);
            return postRepository.findAllPostsOrderByTotalLikesDesc(query, pageable);
        } else if (order.equals("leastLiked")) {
            Pageable pageable = PageRequest.of(page, size);
            return postRepository.findAllPostsOrderByTotalLikesAsc(query, pageable);
        } else if (order.equals("mostReplied")) {
            Pageable pageable = PageRequest.of(page, size);
            return postRepository.findAllPostsOrderByTotalRepliesDesc(query, pageable);
        } else if (order.equals("leastReplied")) {
            Pageable pageable = PageRequest.of(page, size);
            return postRepository.findAllPostsOrderByTotalRepliesAsc(query, pageable);
        } else if (order.equals("oldest")) {
            Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            return postRepository.findAllPosts(query, pageable);
        } else {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            return postRepository.findAllPosts(query, pageable);
        }
    }

    @Override
    public Page<Post> findAllByUserPostedOrderByCreatedAtDesc(User user, Pageable pageable) {
        return postRepository.findAllByUserPostedOrderByCreatedAtDesc(user, pageable);
    }
}
