package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.User;

public interface IBadgeRewardService {
    void rewardBadge(User user, String unit);
}
