package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ISectionRepository extends JpaRepository<Section, UUID> {
    List<Section> findAllByIsDisabled(boolean isDisabled);
}
