package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.constant.AppealReason;
import com.major_project.digital_library.constant.ReportReason;
import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.GeneralStatisticsModel;
import com.major_project.digital_library.model.YearlyStatisticsModel;
import com.major_project.digital_library.repository.*;
import com.major_project.digital_library.service.IStatisticsService;
import com.major_project.digital_library.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements IStatisticsService {
    private static final List<Integer> ALL_MONTHS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
    private final IDocumentRepository documentRepository;
    private final IUserRepository userRepositoty;
    private final IPostRepository postRepository;
    private final IReplyRepository replyRepository;
    private final ICategoryRepository categoryRepository;
    private final IFieldRepository fieldRepository;
    private final IOrganizationRepository organizationRepository;
    private final IReviewRepository reviewRepository;
    private final ISectionRepository sectionRepository;
    private final IPostReportRepository postReportRepository;
    private final IPostAppealRepository postAppealRepository;
    private final IReplyReportRepository replyReportRepository;
    private final IReplyAppealRepository replyAppealRepository;
    private final ISubsectionRepository subsectionRepository;
    private final ILabelRepository labelRepository;
    private final IUserService userService;

    @Autowired
    public StatisticsServiceImpl(IDocumentRepository documentRepository, IUserRepository userRepositoty, IPostRepository postRepository, IReplyRepository replyRepository, ICategoryRepository categoryRepository, IFieldRepository fieldRepository, IOrganizationRepository organizationRepository, IReviewRepository reviewRepository, ISectionRepository sectionRepository, IPostReportRepository postReportRepository, IPostAppealRepository postAppealRepository, IReplyReportRepository replyReportRepository, IReplyAppealRepository replyAppealRepository, ISubsectionRepository subsectionRepository, ILabelRepository labelRepository, IUserService userService) {

        this.documentRepository = documentRepository;
        this.userRepositoty = userRepositoty;
        this.postRepository = postRepository;
        this.replyRepository = replyRepository;
        this.categoryRepository = categoryRepository;
        this.fieldRepository = fieldRepository;
        this.organizationRepository = organizationRepository;
        this.reviewRepository = reviewRepository;
        this.sectionRepository = sectionRepository;
        this.postReportRepository = postReportRepository;
        this.postAppealRepository = postAppealRepository;
        this.replyReportRepository = replyReportRepository;
        this.replyAppealRepository = replyAppealRepository;
        this.subsectionRepository = subsectionRepository;
        this.labelRepository = labelRepository;
        this.userService = userService;
    }

    public List<Timestamp> getDetailDateRange(String dateRange) {
        Timestamp startDate = null;
        Timestamp endDate = null;

        if (dateRange.equals("current")) {
            LocalDate today = LocalDate.now();
            LocalDate startDateOfMonth = LocalDate.now().withDayOfMonth(1);

            startDate = Timestamp.valueOf(today.atStartOfDay());
            endDate = Timestamp.valueOf(today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59));
        } else if (dateRange.equals("1month")) {
            LocalDate startDateOfPreviousMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            LocalDate endDateOfPreviousMonth = LocalDate.now().minusMonths(1).withDayOfMonth(
                    LocalDate.now().minusMonths(1).lengthOfMonth());

            startDate = Timestamp.valueOf(startDateOfPreviousMonth.atStartOfDay());
            endDate = Timestamp.valueOf(endDateOfPreviousMonth.atTime(23, 59, 59));
        } else if (dateRange.equals("3months")) {
            LocalDate startDateOf3LastMonth = LocalDate.now().minusMonths(3).withDayOfMonth(1);
            LocalDate endDateOf3LastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(
                    LocalDate.now().minusMonths(3).lengthOfMonth());

            startDate = Timestamp.valueOf(startDateOf3LastMonth.atStartOfDay());
            endDate = Timestamp.valueOf(endDateOf3LastMonth.atTime(23, 59, 59));
        } else if (dateRange.equals("6months")) {
            LocalDate startDateOf6LastMonth = LocalDate.now().minusMonths(6).withDayOfMonth(1);
            LocalDate endDateOf6LastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(
                    LocalDate.now().minusMonths(6).lengthOfMonth());

            startDate = Timestamp.valueOf(startDateOf6LastMonth.atStartOfDay());
            endDate = Timestamp.valueOf(endDateOf6LastMonth.atTime(23, 59, 59));
        } else if (dateRange.equals("1year")) {
            LocalDate startDateOfPreviousYear = LocalDate.now().minusYears(1).withDayOfMonth(1);

            // Lấy ngày cuối cùng của 12 tháng trước
            LocalDate endDateOfPreviousYear = LocalDate.now().minusMonths(1).withDayOfMonth(
                    LocalDate.now().minusMonths(1).lengthOfMonth());

            startDate = Timestamp.valueOf(startDateOfPreviousYear.atStartOfDay());
            endDate = Timestamp.valueOf(endDateOfPreviousYear.atTime(23, 59, 59));
        }

        return Arrays.asList(startDate, endDate);
    }

    @Override
    public GeneralStatisticsModel getGeneralStatistics(boolean isGeneral, String dateRange, Timestamp startDate, Timestamp endDate) {
        if (isGeneral) {
            List<Timestamp> range = getDetailDateRange(dateRange);
            startDate = range.get(0);
            endDate = range.get(1);
        }

        long totalDocuments = getTotalDocumentCount(null, startDate, endDate);
        long totalPendingDocuments = getPendingDocumentCount(null, startDate, endDate);
        long totalReviews = getTotalReviewCount(null, startDate, endDate);
        long totalPendingReviews = getPendingReviewCount(null, startDate, endDate);
        long totalUsers = getTotalUserCount(null, startDate, endDate);
        long totalCategories = getTotalCategoryCount(startDate, endDate);
        long totalFields = getTotalFieldCount(startDate, endDate);
        long totalOrganizations = getTotalOrganizationCount(startDate, endDate);
        long totalSections = getTotalSectionCount(startDate, endDate);
        long totalSubsections = getTotalSubsectionCount(startDate, endDate);
        long totalLabels = getTotalLabelCount(startDate, endDate);
        long totalPosts = getTotalPostCount(startDate, endDate);
        long totalReplies = getTotalReplyCount(startDate, endDate);
        long totalPostReports = getTotalPostReportCount(startDate, endDate);
        long totalReplyReports = getTotalReplyReportCount(startDate, endDate);
        long totalPostAppeals = getTotalPostAppealCount(startDate, endDate);
        long totalReplyAppeals = getTotalReplyAppealCount(startDate, endDate);
        Map<String, Long> documentCountByCategory = getDocumentCountByCategory(startDate, endDate, null);
        Map<String, Long> documentCountByField = getDocumentCountByField(startDate, endDate, null);
        Map<String, Long> documentCountByOrganization = getDocumentCountByOrganization(startDate, endDate);
        Map<String, Long> userCountByOrganization = getUserCountByOrganization(startDate, endDate);
        Map<String, Long> postCountBySubsection = getPostCountBySubsection(startDate, endDate);
        Map<String, Long> postCountByLabel = getPostCountByLabel(startDate, endDate);
        Map<String, Long> replyCountBySubsection = getReplyCountBySubsection(startDate, endDate);
        Map<String, Long> replyCountByLabel = getReplyCountByLabel(startDate, endDate);
        Map<String, Long> postReportCountByType = getPostReportCountByType(startDate, endDate);
        Map<String, Long> replyReportCountByType = getReplyReportCountByType(startDate, endDate);
        Map<String, Long> postAppealCountByType = getPostAppealCountByType(startDate, endDate);
        Map<String, Long> replyAppealCountByType = getReplyAppealCountByType(startDate, endDate);

        GeneralStatisticsModel generalStatisticsModel = GeneralStatisticsModel.builder()
                .totalDocuments((int) totalDocuments)
                .totalPendingDocuments((int) totalPendingDocuments)
                .totalReviews((int) totalReviews)
                .totalPendingReviews((int) totalPendingReviews)
                .totalUsers((int) totalUsers)
                .totalCategories((int) totalCategories)
                .totalFields((int) totalFields)
                .totalOrganizations((int) totalOrganizations)
                .totalSections((int) totalSections)
                .totalSubsections((int) totalSubsections)
                .totalLabels((int) totalLabels)
                .totalPosts((int) totalPosts)
                .totalReplies((int) totalReplies)
                .totalPostReports((int) totalPostReports)
                .totalReplyReports((int) totalReplyReports)
                .totalPostAppeals((int) totalPostAppeals)
                .totalReplyAppeals((int) totalReplyAppeals)
                .documentsByCategory(documentCountByCategory)
                .documentsByField(documentCountByField)
                .documentsByOrganization(documentCountByOrganization)
                .usersByOrganization(userCountByOrganization)
                .postsBySubsection(postCountBySubsection)
                .postsByLabel(postCountByLabel)
                .repliesBySubsection(replyCountBySubsection)
                .repliesByLabel(replyCountByLabel)
                .postReportsByReason(postReportCountByType)
                .replyReportsByReason(replyReportCountByType)
                .postAppealsByReason(postAppealCountByType)
                .replyAppealsByReason(replyAppealCountByType)
                .build();

        return generalStatisticsModel;
    }

    @Override
    public YearlyStatisticsModel getYearlyStatistics(int year) {
        Map<Integer, Long> documentCountByMonth = getDocumentCountByMonth(year, null);
        Map<Integer, Long> userCountByMonth = getUserCountByMonth(year, null);
        Map<Integer, Long> postCountByMonth = getPostCountByMonth(year);
        Map<Integer, Long> replyCountByMonth = getReplyCountByMonth(year);

        YearlyStatisticsModel yearlyStatisticsModel = YearlyStatisticsModel.builder()
                .documentsByMonth(documentCountByMonth)
                .usersByMonth(userCountByMonth)
                .postsByMonth(postCountByMonth)
                .repliesByMonth(replyCountByMonth)
                .build();

        return yearlyStatisticsModel;
    }

    @Override
    public GeneralStatisticsModel getGeneralStatisticsForManager(boolean isGeneral, String dateRange, Timestamp startDate, Timestamp endDate) {
        if (isGeneral) {
            List<Timestamp> range = getDetailDateRange(dateRange);
            startDate = range.get(0);
            endDate = range.get(1);
        }

        User user = userService.findLoggedInUser();

        long totalDocuments = getTotalDocumentCount(user.getOrganization(), startDate, endDate);
        long totalPendingDocuments = getPendingDocumentCount(user.getOrganization(), startDate, endDate);
        long totalReviews = getTotalReviewCount(null, startDate, endDate);
        long totalPendingReviews = getPendingReviewCount(null, startDate, endDate);
        long totalUsers = getTotalUserCount(user.getOrganization(), startDate, endDate);

        Map<String, Long> documentCountByCategory = getDocumentCountByCategory(startDate, endDate, user.getOrganization());
        Map<String, Long> documentCountByField = getDocumentCountByField(startDate, endDate, user.getOrganization());

        GeneralStatisticsModel generalStatisticsModel = GeneralStatisticsModel.builder()
                .totalDocuments((int) totalDocuments)
                .totalPendingDocuments((int) totalPendingDocuments)
                .totalReviews((int) totalReviews)
                .totalPendingReviews((int) totalPendingReviews)
                .totalUsers((int) totalUsers)
                .documentsByCategory(documentCountByCategory)
                .documentsByField(documentCountByField)
                .build();

        return generalStatisticsModel;
    }

    @Override
    public YearlyStatisticsModel getYearlyStatisticsForManager(int year) {
        User user = userService.findLoggedInUser();

        Map<Integer, Long> documentCountByMonth = getDocumentCountByMonth(year, user.getOrganization());
        Map<Integer, Long> userCountByMonth = getUserCountByMonth(year, user.getOrganization());

        YearlyStatisticsModel yearlyStatisticsModel = YearlyStatisticsModel.builder()
                .documentsByMonth(documentCountByMonth)
                .usersByMonth(userCountByMonth)
                .build();

        return yearlyStatisticsModel;
    }

    public long getTotalUserCount(Organization organization, Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (organization == null) {
            if (startDate == null && endDate == null)
                count = userRepositoty.count();
            else
                count = userRepositoty.countByCreatedAtBetween(startDate, endDate);
        } else {
            if (startDate == null && endDate == null)
                count = userRepositoty.countByOrganization(organization);
            else
                count = userRepositoty.countByOrganizationAndCreatedAtBetween(organization, startDate, endDate);
        }

        return count;
    }

    public long getTotalDocumentCount(Organization organization, Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (organization == null) {
            if (startDate == null && endDate == null)
                count = documentRepository.count();
            else
                count = documentRepository.countByUploadedAtBetween(startDate, endDate);
        } else {
            if (startDate == null && endDate == null)
                count = documentRepository.countByOrganization(organization);
            else
                count = documentRepository.countByOrganizationAndUploadedAtBetween(organization, startDate, endDate);
        }

        return count;
    }

    public long getTotalCategoryCount(Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (startDate == null && endDate == null)
            count = categoryRepository.count();
        else
            count = categoryRepository.countByCreatedAtBetween(startDate, endDate);

        return count;
    }

    public long getTotalFieldCount(Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (startDate == null && endDate == null)
            count = fieldRepository.count();
        else
            count = fieldRepository.countByCreatedAtBetween(startDate, endDate);

        return count;
    }

    public long getTotalOrganizationCount(Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (startDate == null && endDate == null)
            count = organizationRepository.count();
        else
            count = organizationRepository.countByCreatedAtBetween(startDate, endDate);

        return count;
    }

    public long getTotalReviewCount(Organization organization, Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (organization == null) {
            if (startDate == null && endDate == null)
                count = reviewRepository.count();
            else
                count = reviewRepository.countByCreatedAtBetween(startDate, endDate);
        } else {
            if (startDate == null && endDate == null)
                count = reviewRepository.countByDocumentOrganization(organization);
            else
                count = reviewRepository.countByDocumentOrganizationAndCreatedAtBetween(organization, startDate, endDate);
        }

        return count;
    }

    public long getTotalPostCount(Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (startDate == null && endDate == null)
            count = postRepository.count();
        else
            count = postRepository.countByCreatedAtBetween(startDate, endDate);

        return count;
    }

    public long getTotalReplyCount(Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (startDate == null && endDate == null)
            count = replyRepository.count();
        else
            count = replyRepository.countByCreatedAtBetween(startDate, endDate);

        return count;
    }

    public long getTotalSectionCount(Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (startDate == null && endDate == null)
            count = sectionRepository.count();
        else
            count = sectionRepository.countByCreatedAtBetween(startDate, endDate);

        return count;
    }

    public long getTotalSubsectionCount(Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (startDate == null && endDate == null)
            count = subsectionRepository.count();
        else
            count = subsectionRepository.countByCreatedAtBetween(startDate, endDate);

        return count;
    }

    public long getTotalLabelCount(Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (startDate == null && endDate == null)
            count = labelRepository.count();
        else
            count = labelRepository.countByCreatedAtBetween(startDate, endDate);

        return count;
    }

    public long getPendingDocumentCount(Organization organization, Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (organization == null) {
            if (startDate == null && endDate == null)
                count = documentRepository.countByVerifiedStatus(0);
            else
                count = documentRepository.countByVerifiedStatusAndUploadedAtBetween(0, startDate, endDate);
        } else {
            if (startDate == null && endDate == null)
                count = documentRepository.countByVerifiedStatusAndOrganization(0, organization);
            else
                count = documentRepository.countByVerifiedStatusAndOrganizationAndUploadedAtBetween(0, organization, startDate, endDate);

        }

        return count;
    }

    public long getPendingReviewCount(Organization organization, Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (organization == null) {
            if (startDate == null && endDate == null)
                count = reviewRepository.countByVerifiedStatus(0);
            else
                count = reviewRepository.countByVerifiedStatusAndCreatedAtBetween(0, startDate, endDate);
        } else {
            if (startDate == null && endDate == null)
                count = reviewRepository.countByVerifiedStatusAndDocumentOrganization(0, organization);
            else
                count = reviewRepository.countByVerifiedStatusAndDocumentOrganizationAndCreatedAtBetween(0, organization, startDate, endDate);

        }

        return count;
    }

    public long getTotalPostReportCount(Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (startDate == null && endDate == null)
            count = postReportRepository.count();
        else
            count = postReportRepository.countByReportedAtBetween(startDate, endDate);

        return count;
    }

    public long getTotalReplyReportCount(Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (startDate == null && endDate == null)
            count = replyReportRepository.count();
        else
            count = replyReportRepository.countByReportedAtBetween(startDate, endDate);

        return count;
    }

    public long getTotalPostAppealCount(Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (startDate == null && endDate == null)
            count = postAppealRepository.count();
        else
            count = postAppealRepository.countByAppealedAtBetween(startDate, endDate);

        return count;
    }

    public long getTotalReplyAppealCount(Timestamp startDate, Timestamp endDate) {
        long count = 0;

        if (startDate == null && endDate == null)
            count = replyAppealRepository.count();
        else
            count = replyAppealRepository.countByAppealedAtBetween(startDate, endDate);

        return count;
    }

    public Map<Integer, Long> getDocumentCountByMonth(int year, Organization organization) {
        List<Object[]> documentsByMonth = documentRepository.countDocumentsByMonth(year, organization);
        Map<Integer, Long> documentCountByMonth = documentsByMonth.stream()
                .collect(Collectors.toMap(
                        result -> ((Number) result[0]).intValue(),
                        result -> (Long) result[1]
                ));

        ALL_MONTHS.forEach(month -> documentCountByMonth.putIfAbsent(month, 0L));
        return documentCountByMonth;
    }

    public Map<String, Long> getDocumentCountByCategory(Timestamp startDate, Timestamp endDate, Organization organization) {
        List<Object[]> documentsByCategory = new ArrayList<>();

        if (startDate == null && endDate == null)
            documentsByCategory = documentRepository.countDocumentsByCategory(organization);
        else
            documentsByCategory = documentRepository.countDocumentsByCategoryAndDateRange(startDate, endDate, organization);

        Map<String, Long> documentCountByCategory = documentsByCategory.stream()
                .collect(Collectors.toMap(
                        result -> result[0].toString(),
                        result -> (Long) result[1]
                ));

        return documentCountByCategory;
    }

    public Map<String, Long> getDocumentCountByField(Timestamp startDate, Timestamp endDate, Organization organization) {
        List<Object[]> documentsByField = new ArrayList<>();

        if (startDate == null && endDate == null)
            documentsByField = documentRepository.countDocumentsByField(organization);
        else
            documentsByField = documentRepository.countDocumentsByFieldAndDateRange(startDate, endDate, organization);

        Map<String, Long> documentCountByField = documentsByField.stream()
                .collect(Collectors.toMap(
                        result -> result[0].toString(),
                        result -> (Long) result[1]
                ));

        return documentCountByField;
    }

    public Map<String, Long> getDocumentCountByOrganization(Timestamp startDate, Timestamp endDate) {
        List<Object[]> documentsByOrganization = new ArrayList<>();

        if (startDate == null && endDate == null)
            documentsByOrganization = documentRepository.countDocumentsByOrganization();
        else
            documentsByOrganization = documentRepository.countDocumentsByOrganizationAndDateRange(startDate, endDate);

        Map<String, Long> documentCountByOrganization = documentsByOrganization.stream()
                .collect(Collectors.toMap(
                        result -> result[0].toString(),
                        result -> (Long) result[1]
                ));

        return documentCountByOrganization;
    }

    public Map<Integer, Long> getUserCountByMonth(int year, Organization organization) {
        List<Object[]> usersByMonth = userRepositoty.countUsersByMonth(year, organization);
        Map<Integer, Long> userCountByMonth = usersByMonth.stream()
                .collect(Collectors.toMap(
                        result -> ((Number) result[0]).intValue(), // Tháng
                        result -> (Long) result[1] // Số lượng user
                ));

        ALL_MONTHS.forEach(month -> userCountByMonth.putIfAbsent(month, 0L));
        return userCountByMonth;
    }

    public Map<String, Long> getUserCountByOrganization(Timestamp startDate, Timestamp endDate) {
        List<Object[]> usersByOrganization = new ArrayList<>();

        if (startDate == null && endDate == null)
            usersByOrganization = userRepositoty.countUsersByOrganization();
        else
            usersByOrganization = userRepositoty.countUsersByOrganizationAndDateRange(startDate, endDate);

        Map<String, Long> userCountByOrganization = usersByOrganization.stream()
                .collect(Collectors.toMap(
                        result -> result[0].toString(),
                        result -> (Long) result[1]
                ));

        return userCountByOrganization;
    }

    public Map<Integer, Long> getPostCountByMonth(int year) {
        List<Object[]> postsByMonth = postRepository.countPostsByMonth(year);
        Map<Integer, Long> postCountByMonth = postsByMonth.stream()
                .collect(Collectors.toMap(
                        result -> ((Number) result[0]).intValue(),
                        result -> (Long) result[1]
                ));

        ALL_MONTHS.forEach(month -> postCountByMonth.putIfAbsent(month, 0L));
        return postCountByMonth;
    }

    public Map<String, Long> getPostCountBySubsection(Timestamp startDate, Timestamp endDate) {
        List<Object[]> postsByCategory = new ArrayList<>();

        if (startDate == null && endDate == null)
            postsByCategory = postRepository.countPostsBySubsection();
        else
            postsByCategory = postRepository.countPostsBySubsectionAndDateRange(startDate, endDate);

        Map<String, Long> postCountByCategory = postsByCategory.stream()
                .collect(Collectors.toMap(
                        result -> result[0].toString(),
                        result -> (Long) result[1]
                ));

        return postCountByCategory;
    }

    public Map<String, Long> getPostCountByLabel(Timestamp startDate, Timestamp endDate) {
        List<Object[]> postsByField = new ArrayList<>();
        long postCountWithNoLabel = 0;

        if (startDate == null && endDate == null) {
            postsByField = postRepository.countPostsByLabel();
            postCountWithNoLabel = postRepository.countPostsWithNoLabel();
        } else {
            postsByField = postRepository.countPostsByLabelAndDateRange(startDate, endDate);
            postCountWithNoLabel = postRepository.countPostsWithNoLabelAndDateRange(startDate, endDate);
        }

        Map<String, Long> postCountByField = postsByField.stream()
                .collect(Collectors.toMap(
                        result -> result[0].toString(),
                        result -> (Long) result[1]
                ));
        if (!postCountByField.isEmpty())
            postCountByField.put("không nhãn", postCountWithNoLabel);

        return postCountByField;
    }

    public Map<Integer, Long> getReplyCountByMonth(int year) {
        List<Object[]> repliesByMonth = replyRepository.countRepliesByMonth(year);
        Map<Integer, Long> replyCountByMonth = repliesByMonth.stream()
                .collect(Collectors.toMap(
                        result -> ((Number) result[0]).intValue(),
                        result -> (Long) result[1]
                ));

        ALL_MONTHS.forEach(month -> replyCountByMonth.putIfAbsent(month, 0L));
        return replyCountByMonth;
    }

    public Map<String, Long> getReplyCountBySubsection(Timestamp startDate, Timestamp endDate) {
        List<Object[]> repliesByCategory = new ArrayList<>();

        if (startDate == null && endDate == null)
            repliesByCategory = replyRepository.countRepliesBySubsection();
        else
            repliesByCategory = replyRepository.countRepliesBySubsectionAndDateRange(startDate, endDate);

        Map<String, Long> replyCountByCategory = repliesByCategory.stream()
                .collect(Collectors.toMap(
                        result -> result[0].toString(),
                        result -> (Long) result[1]
                ));

        return replyCountByCategory;
    }

    public Map<String, Long> getReplyCountByLabel(Timestamp startDate, Timestamp endDate) {
        List<Object[]> repliesByField = new ArrayList<>();
        long replyCountWithNoLabel = 0;

        if (startDate == null && endDate == null) {
            repliesByField = replyRepository.countRepliesByLabel();
            replyCountWithNoLabel = postRepository.countPostsWithNoLabel();
        } else {
            repliesByField = replyRepository.countRepliesByLabelAndDateRange(startDate, endDate);
            replyCountWithNoLabel = postRepository.countPostsWithNoLabelAndDateRange(startDate, endDate);
        }

        Map<String, Long> replyCountByField = repliesByField.stream()
                .collect(Collectors.toMap(
                        result -> result[0].toString(),
                        result -> (Long) result[1]
                ));
        if (!replyCountByField.isEmpty())
            replyCountByField.put("không nhãn", replyCountWithNoLabel);

        return replyCountByField;
    }

    public Map<String, Long> getPostReportCountByType(Timestamp startDate, Timestamp endDate) {
        List<Object[]> postReportsByType = new ArrayList<>();

        if (startDate == null && endDate == null)
            postReportsByType = postReportRepository.countPostReportsByType();
        else
            postReportsByType = postReportRepository.countPostReportsByTypeAndDateRange(startDate, endDate);

        Map<String, Long> postReportCountByType = postReportsByType.stream()
                .collect(Collectors.toMap(
                        result -> ReportReason.valueOf(result[0].toString()).getMessage(),
                        result -> (Long) result[1]
                ));

        return postReportCountByType;
    }

    public Map<String, Long> getReplyReportCountByType(Timestamp startDate, Timestamp endDate) {
        List<Object[]> replyReportsByType = new ArrayList<>();

        if (startDate == null && endDate == null)
            replyReportsByType = replyReportRepository.countReplyReportsByType();
        else
            replyReportsByType = replyReportRepository.countReplyReportsByTypeAndDateRange(startDate, endDate);

        Map<String, Long> replyReportCountByType = replyReportsByType.stream()
                .collect(Collectors.toMap(
                        result -> ReportReason.valueOf(result[0].toString()).getMessage(),
                        result -> (Long) result[1]
                ));

        return replyReportCountByType;
    }

    public Map<String, Long> getPostAppealCountByType(Timestamp startDate, Timestamp endDate) {
        List<Object[]> postAppealsByType = new ArrayList<>();

        if (startDate == null && endDate == null)
            postAppealsByType = postAppealRepository.countPostAppealsByType();
        else
            postAppealsByType = postAppealRepository.countPostAppealsByTypeAndDateRange(startDate, endDate);

        Map<String, Long> postAppealCountByType = postAppealsByType.stream()
                .collect(Collectors.toMap(
                        result -> AppealReason.valueOf(result[0].toString()).getMessage(),
                        result -> (Long) result[1]
                ));

        return postAppealCountByType;
    }

    public Map<String, Long> getReplyAppealCountByType(Timestamp startDate, Timestamp endDate) {
        List<Object[]> replyAppealsByType = new ArrayList<>();

        if (startDate == null && endDate == null)
            replyAppealsByType = replyAppealRepository.countReplyAppealsByType();
        else
            replyAppealsByType = replyAppealRepository.countReplyAppealsByTypeAndDateRange(startDate, endDate);

        Map<String, Long> replyAppealCountByType = replyAppealsByType.stream()
                .collect(Collectors.toMap(
                        result -> AppealReason.valueOf(result[0].toString()).getMessage(),
                        result -> (Long) result[1]
                ));

        return replyAppealCountByType;
    }
}
