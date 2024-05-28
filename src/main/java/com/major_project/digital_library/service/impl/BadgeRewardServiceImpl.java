package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.constant.BadgeUnit;
import com.major_project.digital_library.constant.NotificationMessage;
import com.major_project.digital_library.entity.*;
import com.major_project.digital_library.repository.IBadgeRepository;
import com.major_project.digital_library.repository.IBadgeRewardRepository;
import com.major_project.digital_library.repository.IBadgeTypeRepository;
import com.major_project.digital_library.repository.IUserRepository;
import com.major_project.digital_library.service.IBadgeRewardService;
import com.major_project.digital_library.service.INotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class BadgeRewardServiceImpl implements IBadgeRewardService {
    private final IBadgeRewardRepository badgeRewardRepository;
    private final IBadgeRepository badgeRepository;
    private final IBadgeTypeRepository badgeTypeRepository;
    private final IUserRepository userRepositoty;
    private final INotificationService notificationService;
    private final ModelMapper modelMapper;

    @Autowired
    public BadgeRewardServiceImpl(IBadgeRewardRepository badgeRewardRepository, IBadgeRepository badgeRepository, IBadgeTypeRepository badgeTypeRepository, IUserRepository userRepositoty, INotificationService notificationService, ModelMapper modelMapper) {
        this.badgeRewardRepository = badgeRewardRepository;
        this.badgeRepository = badgeRepository;
        this.badgeTypeRepository = badgeTypeRepository;
        this.userRepositoty = userRepositoty;
        this.notificationService = notificationService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void rewardBadge(User user, String unit) {
        BadgeType badgeType = badgeTypeRepository.findByUnit(unit).orElseThrow(() -> new RuntimeException("Badge type not found"));
        int value = 0;
        if (unit.equals(BadgeUnit.TOTAL_POSTS.name()))
            value = user.getPosts().size();
        else if (unit.equals(BadgeUnit.TOTAL_REPLIES.name()))
            value = user.getReplies().size();
        else if (unit.equals(BadgeUnit.TOTAL_POST_LIKES.name()))
            value = user.getPosts().stream()
                    .flatMapToInt(post -> IntStream.of(post.getPostLikes().size()))
                    .sum();
        else if (unit.equals(BadgeUnit.TOTAL_REPLY_LIKES.name()))
            value = user.getReplies().stream()
                    .flatMapToInt(reply -> IntStream.of(reply.getReplyLikes().size()))
                    .sum();
        else
            value = user.getPosts().stream().mapToInt(Post::getTotalViews).sum();

        Optional<Badge> badge = badgeRepository.findByBadgeTypeAndValue(badgeType, value);
        if (badge.isPresent()) {
            boolean isRewarded = badgeRewardRepository.existsByUserAndBadge(user, badge.get());
            if (!isRewarded) {
                BadgeReward badgeReward = new BadgeReward();
                badgeReward.setBadge(badge.get());
                badgeReward.setUser(user);
                badgeRewardRepository.save(badgeReward);

                notificationService.sendNotification(NotificationMessage.REWARD_BADGE.name(), NotificationMessage.REWARD_BADGE.getMessage(), user, user, badge);
            }
        }
    }
}
