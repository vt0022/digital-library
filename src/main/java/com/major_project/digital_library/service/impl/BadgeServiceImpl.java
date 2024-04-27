package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Badge;
import com.major_project.digital_library.entity.BadgeReward;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.lean_model.BadgeLeanModel;
import com.major_project.digital_library.model.response_model.BadgeResponseModel;
import com.major_project.digital_library.repository.IBadgeRepository;
import com.major_project.digital_library.repository.IBadgeRewardRepository;
import com.major_project.digital_library.repository.IUserRepositoty;
import com.major_project.digital_library.service.IBadgeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BadgeServiceImpl implements IBadgeService {
    private final IBadgeRepository badgeRepository;
    private final IBadgeRewardRepository badgeRewardRepository;
    private final IUserRepositoty userRepositoty;
    private final ModelMapper modelMapper;

    @Autowired
    public BadgeServiceImpl(IBadgeRepository badgeRepository, IBadgeRewardRepository badgeRewardRepository, IUserRepositoty userRepositoty, ModelMapper modelMapper) {
        this.badgeRepository = badgeRepository;
        this.badgeRewardRepository = badgeRewardRepository;
        this.userRepositoty = userRepositoty;
        this.modelMapper = modelMapper;
    }

    public <S extends Badge> S save(S entity) {
        return badgeRepository.save(entity);
    }

    @Override
    public List<BadgeResponseModel> findBadgesOfUser(UUID userId) {
        User user = userRepositoty.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<BadgeReward> badgeRewards = badgeRewardRepository.findByUser(user);
        List<BadgeResponseModel> badgeResponseModels = new ArrayList<>();
        for (BadgeReward badgeReward : badgeRewards) {
            BadgeResponseModel badgeResponseModel = modelMapper.map(badgeReward.getBadge(), BadgeResponseModel.class);
            badgeResponseModel.setRewardedAt(badgeReward.getRewardedAt());
            badgeResponseModels.add(badgeResponseModel);
        }
        return badgeResponseModels;
    }

    @Override
    public BadgeLeanModel findBestBadge(UUID userId) {
        User user = userRepositoty.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Badge badge = badgeRepository.findBestBadgeByUser(user).orElse(null);

        BadgeLeanModel badgeLeanModel = badge == null ? null : modelMapper.map(badge, BadgeLeanModel.class);
        return badgeLeanModel;
    }
}
