package com.major_project.digital_library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Subsection {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID subId;

    private String subName;

    private String slug;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean isDisabled;

    private boolean isEditable;

    @ManyToOne
    @JoinColumn(name = "sectionId")
    private Section section;

    @OneToMany(mappedBy = "subsection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
