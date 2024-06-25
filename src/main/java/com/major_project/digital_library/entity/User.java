package com.major_project.digital_library.entity;

import com.major_project.digital_library.model.response_model.UserReputationResponseModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "UserReputationMapping",
                classes = @ConstructorResult(
                        targetClass = UserReputationResponseModel.class,
                        columns = {
                                @ColumnResult(name = "userId", type = UUID.class),
                                @ColumnResult(name = "firstName", type = String.class),
                                @ColumnResult(name = "lastName", type = String.class),
                                @ColumnResult(name = "image", type = String.class),
                                @ColumnResult(name = "email", type = String.class),
                                @ColumnResult(name = "createdAt", type = Timestamp.class),
                                @ColumnResult(name = "totalPostAcceptances", type = Integer.class),
                                @ColumnResult(name = "totalReplyAcceptances", type = Integer.class),
                                @ColumnResult(name = "totalPostLikes", type = Integer.class),
                                @ColumnResult(name = "totalReplyLikes", type = Integer.class),
                                @ColumnResult(name = "totalScores", type = Integer.class),
                        }
                )
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "User.findUsersForReputationByYear",
                query = "SELECT u.user_id userId, u.first_name firstName, u.last_name lastName, u.image, u.email, u.created_at createdAt, " +
                        "COALESCE(pa.totalPostAcceptances, 0) totalPostAcceptances, COALESCE(ra.totalReplyAcceptances, 0) totalReplyAcceptances, " +
                        "COALESCE(pl.totalPostLikes, 0) totalPostLikes, COALESCE(rl.totalReplyLikes, 0) totalReplyLikes, " +
                        "(COALESCE(pa.totalPostAcceptances, 0) * 10 + COALESCE(ra.totalReplyAcceptances, 0) * 10 + COALESCE(pl.totalPostLikes, 0) * 2 + COALESCE(rl.totalReplyLikes, 0) * 2) totalScores " +
                        "FROM User u " +
                        "LEFT JOIN (SELECT Post.posted_by, COUNT(*) totalPostAcceptances FROM Post JOIN Post_Acceptance ON Post_Acceptance.post_id = Post.post_id WHERE Post.is_disabled = FALSE AND YEAR(Post_Acceptance.accepted_at) = ?2 GROUP BY Post.posted_by) pa ON u.user_id = pa.posted_by " +
                        "LEFT JOIN (SELECT Reply.replied_by, COUNT(*) totalReplyAcceptances FROM Reply JOIN Reply_Acceptance ON Reply_Acceptance.reply_id = Reply.reply_id WHERE Reply.is_disabled = FALSE AND YEAR(Reply_Acceptance.accepted_at) = ?2 GROUP BY Reply.replied_by) ra ON u.user_id = ra.replied_by " +
                        "LEFT JOIN (SELECT Post.posted_by, COUNT(*) totalPostLikes FROM Post JOIN Post_Like ON Post_Like.post_id = Post.post_id WHERE Post.is_disabled = FALSE AND YEAR(Post_Like.liked_at) = ?2 GROUP BY Post.posted_by) pl ON u.user_id = pl.posted_by " +
                        "LEFT JOIN (SELECT Reply.replied_by, COUNT(*) totalReplyLikes FROM Reply JOIN Reply_Like ON Reply_Like.reply_id = Reply.reply_id WHERE Reply.is_disabled = FALSE AND YEAR(Reply_Like.liked_at) = ?2 GROUP BY Reply.replied_by) rl ON u.user_id = rl.replied_by " +
                        "WHERE u.is_disabled = FALSE " +
                        "AND u.is_authenticated = TRUE " +
                        "AND (LOWER(u.first_name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
                        "OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
                        "GROUP BY u.user_id " +
                        "ORDER BY " +
                        "(COALESCE(pa.totalPostAcceptances, 0) * 10 + " +
                        "COALESCE(ra.totalReplyAcceptances, 0) * 10 + " +
                        "COALESCE(pl.totalPostLikes, 0) * 2 + " +
                        "COALESCE(rl.totalReplyLikes, 0) * 2) DESC, " +
                        "COALESCE(pa.totalPostAcceptances, 0) DESC, " +
                        "COALESCE(ra.totalReplyAcceptances, 0) DESC, " +
                        "COALESCE(pl.totalPostLikes, 0) DESC, " +
                        "COALESCE(rl.totalReplyLikes, 0) DESC, " +
                        "u.created_at DESC"
        )
})
@Entity
public class User implements Serializable, UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID userId;

    @Column(length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    private Timestamp dateOfBirth;

    private int gender;

    private String image;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, length = 50, nullable = false)
    private String email;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean isDisabled;

    private boolean isAuthenticated;

    @ManyToOne
    @JoinColumn(name = "orgId")
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;

    @OneToMany(mappedBy = "userUploaded", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> uploadedDocuments;

    @OneToMany(mappedBy = "userVerified", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Document> verifiedDocuments;

    @OneToMany(mappedBy = "userVerified", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Review> verifiedReviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentLike> documentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentNote> documentNotes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Save> saves = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recency> recencies = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private VerificationCode verificationCode;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplyLike> replyLikes = new ArrayList<>();

    @OneToMany(mappedBy = "userPosted", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BadgeReward> badgeRewards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<com.major_project.digital_library.entity.Collection> collections = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CollectionLike> collectionLikes = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> sentNotifications = new ArrayList<>();

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> receivedNotifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostReport> postReports = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostAcceptance> postAcceptances = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplyAcceptance> replyAcceptances = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority((role.getRoleName())));
        return List.of(new SimpleGrantedAuthority(authorities.toString()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
