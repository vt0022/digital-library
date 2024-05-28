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
public class Post {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID postId;

    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private int totalViews;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean isDisabled;

    @ManyToOne
    @JoinColumn(name = "postedBy")
    private User userPosted;

    @ManyToOne
    @JoinColumn(name = "subsectionId")
    private Subsection subsection;

    @ManyToOne
    @JoinColumn(name = "labelId")
    private Label label;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImages = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHistory> postHistories = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.MERGE)
    private List<PostReport> postReports = new ArrayList<>();

    @ManyToMany()
    @JoinTable(
            name = "post_tag",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private List<Tag> tags = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }
}
