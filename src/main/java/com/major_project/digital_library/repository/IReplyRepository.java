package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IReplyRepository extends JpaRepository<Reply, UUID> {
    Page<Reply> findAllByPostOrderByCreatedAtAsc(Post post, Pageable pageable);
}
