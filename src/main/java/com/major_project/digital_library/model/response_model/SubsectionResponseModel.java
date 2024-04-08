package com.major_project.digital_library.model.response_model;

import com.major_project.digital_library.model.lean_model.PostLeanModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubsectionResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID subId;

    private String subName;

    private String slug;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private int totalPosts;

    private int totalReplies;

    private PostLeanModel latestPost;
}
