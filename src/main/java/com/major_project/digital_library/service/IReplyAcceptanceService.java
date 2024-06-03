package com.major_project.digital_library.service;

import java.util.UUID;

public interface IReplyAcceptanceService {
    void doAccept(UUID replyId);

    void undoAccept(UUID replyId);
}
