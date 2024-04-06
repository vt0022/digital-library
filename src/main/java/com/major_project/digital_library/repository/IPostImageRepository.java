package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IPostImageRepository extends JpaRepository<PostImage, UUID> {
}