package com.major_project.digital_library.model.request_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyReportRequestModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID replyId;

    private String type;

    private String reason = "";
}
