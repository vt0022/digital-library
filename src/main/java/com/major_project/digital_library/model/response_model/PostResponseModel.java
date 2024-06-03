package com.major_project.digital_library.model.response_model;

import com.major_project.digital_library.model.lean_model.LabelLeanModel;
import com.major_project.digital_library.model.lean_model.ReplyLeanModel;
import com.major_project.digital_library.model.lean_model.SubsectionLeanModel;
import com.major_project.digital_library.model.lean_model.UserLeanModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID postId;

    private String title;

    private String content;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean isDisabled;

    private boolean isLabelDisabled;

    private boolean isSectionDisabled;

    private boolean isSubsectionDisabled;

    private UserLeanModel userPosted;

    private int totalReplies;

    private int totalLikes;

    private int totalViews;

    private ReplyLeanModel latestReply;

    private LabelLeanModel label;

    private SubsectionLeanModel subsection;

    private List<String> peopleLiked;

    private List<String> peopleAccepted;
}
