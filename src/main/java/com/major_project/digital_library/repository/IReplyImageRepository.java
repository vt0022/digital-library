package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.ReplyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IReplyImageRepository extends JpaRepository<ReplyImage, UUID> {
}
