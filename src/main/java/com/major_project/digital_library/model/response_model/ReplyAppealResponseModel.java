package com.major_project.digital_library.model.response_model;

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
public class ReplyAppealResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID appealId;

    private Timestamp appealedAt;

    private String status;

    private String disableReason;

    private String type;

    private String reason;

    private ReplyReportResponseModel replyReport;

    private UserLeanModel user;
}
