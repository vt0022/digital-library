package com.major_project.digital_library.service;

import com.major_project.digital_library.model.lean_model.BadgeLeanModel;
import com.major_project.digital_library.model.response_model.BadgeResponseModel;

import java.util.List;
import java.util.UUID;

public interface IBadgeService {
    List<BadgeResponseModel> findBadgesOfUser(UUID userId);

    BadgeLeanModel findBestBadge(UUID userId);
}
