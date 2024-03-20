package com.major_project.digital_library.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class PostImage {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID imageId;

    private String url;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;
}
