package com.major_project.digital_library.model.response_model;

import com.major_project.digital_library.model.lean_model.LabelLeanModel;
import com.major_project.digital_library.model.lean_model.SubsectionLeanModel;
import com.major_project.digital_library.model.lean_model.UserLeanModel;
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
public class DetailPostResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID postId;

    private String title;

    private String content;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private UserLeanModel userPosted;

    private int totalReplies;

    private int totalLikes;

    private boolean isLiked;

    private int totalViews;

    private boolean isMy;

    private SubsectionLeanModel subsection;

    private LabelLeanModel label;
}