package com.major_project.digital_library.service;

import java.util.UUID;

public interface IPostAcceptanceService {
    void doAccept(UUID postId);

    void undoAccept(UUID postId);
}
