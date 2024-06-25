package com.major_project.digital_library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Document {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID docId;

    @Column(nullable = false)
    private String docName;

    @Column(length = 65535)
    private String docIntroduction;

    @Column(nullable = false)
    private String viewUrl;

    @Column(nullable = false)
    private String downloadUrl;

    @Column(nullable = false, unique = true)
    private String slug;

    private Timestamp uploadedAt;

    private Timestamp updatedAt;

    private Timestamp verifiedAt;

    private boolean isDeleted;

    private int verifiedStatus;

    private String note;

    private int totalView;

    private boolean isInternal;

    private boolean isContributed;

    private String thumbnail;

    private int totalPages;

    private String fileId;

    @ManyToOne
    @JoinColumn(name = "uploadedBy")
    private User userUploaded;

    @ManyToOne
    @JoinColumn(name = "verifiedBy")
    private User userVerified;

    @ManyToOne
    @JoinColumn(name = "orgId")
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "fieldId")
    private Field field;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Save> saves = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentLike> documentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recency> recencies = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "document_tag",
            joinColumns = {@JoinColumn(name = "doc_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CollectionDocument> collectionDocuments = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentNote> documentNotes = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        uploadedAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = new Timestamp(System.currentTimeMillis());
//    }

}
