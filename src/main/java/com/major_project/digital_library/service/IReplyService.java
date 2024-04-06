package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IReplyService {
    Page<Reply> findAllByPostOrderByCreatedAtAsc(Post post, Pageable pageable);

    <S extends Reply> S save(S entity);

    Optional<Reply> findById(UUID uuid);

    Page<Reply> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    void deleteById(UUID uuid);
}
