package com.major_project.digital_library.model.response_model;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.model.lean_model.PostLeanModel;
import com.major_project.digital_library.model.lean_model.ReplyLeanModel;
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
public class NotificationResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID notiId;

    private boolean isRead;

    private String type;

    private String message;

    private Timestamp sentAt;

    private UserLeanModel sender;

    private UserLeanModel recipient;

    private Document document;

    private PostLeanModel post;

    private ReplyLeanModel reply;

    private PostReportResponseModel postReport;

    private ReplyReportResponseModel replyReport;

    private PostAppealResponseModel postAppeal;

    private ReplyAppealResponseModel replyAppeal;

    private BadgeResponseModel badge;
}
