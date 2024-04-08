package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IReplyHistoryRepository extends JpaRepository<ReplyHistory, UUID> {
    List<ReplyHistory> findAllByReplyOrderByLoggedAtDesc(Reply reply);
}
