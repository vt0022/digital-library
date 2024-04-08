package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.PostHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IPostHistoryRepository extends JpaRepository<PostHistory, UUID> {

    List<PostHistory> findAllByPostOrderByLoggedAtDesc(Post post);
}
