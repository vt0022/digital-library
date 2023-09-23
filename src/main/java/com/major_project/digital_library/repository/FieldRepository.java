package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FieldRepository extends JpaRepository<Field, UUID> {
}
