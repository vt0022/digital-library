package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.StatisticsModel;
import com.major_project.digital_library.repository.IDocumentRepository;
import com.major_project.digital_library.repository.IUserRepositoty;
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
    private final IUserRepositoty userRepositoty;
    private final IUserService userService;

    @Autowired
    public StatisticsServiceImpl(IDocumentRepository documentRepository, IUserRepositoty userRepositoty, IUserService userService) {

        this.documentRepository = documentRepository;
        this.userRepositoty = userRepositoty;
        this.userService = userService;
    }

    public List<Timestamp> getDetailDateRange(String dateRange) {
        Timestamp startDate = null;
        Timestamp endDate = null;

        if (dateRange.equals("current")) {
            LocalDate today = LocalDate.now();
            LocalDate startDateOfMonth = LocalDate.now().withDayOfMonth(1);

            startDate = Timestamp.valueOf(today.atStartOfDay());
            endDate = Timestamp.valueOf(startDateOfMonth.atStartOfDay());
        } else if (dateRange.equals("1month")) {
            LocalDate startDateOfPreviousMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            LocalDate endDateOfPreviousMonth = LocalDate.now().minusMonths(1).withDayOfMonth(
                    LocalDate.now().minusMonths(1).lengthOfMonth());

            startDate = Timestamp.valueOf(startDateOfPreviousMonth.atStartOfDay());
            endDate = Timestamp.valueOf(endDateOfPreviousMonth.atStartOfDay());
        } else if (dateRange.equals("3months")) {
            LocalDate startDateOf3LastMonth = LocalDate.now().minusMonths(3).withDayOfMonth(1);
            LocalDate endDateOf3LastMonth = LocalDate.now().minusMonths(3).withDayOfMonth(
                    LocalDate.now().minusMonths(3).lengthOfMonth());

            startDate = Timestamp.valueOf(startDateOf3LastMonth.atStartOfDay());
            endDate = Timestamp.valueOf(endDateOf3LastMonth.atStartOfDay());
        } else if (dateRange.equals("6months")) {
            LocalDate startDateOf6LastMonth = LocalDate.now().minusMonths(6).withDayOfMonth(1);
            LocalDate endDateOf6LastMonth = LocalDate.now().minusMonths(6).withDayOfMonth(
                    LocalDate.now().minusMonths(6).lengthOfMonth());

            startDate = Timestamp.valueOf(startDateOf6LastMonth.atStartOfDay());
            endDate = Timestamp.valueOf(endDateOf6LastMonth.atStartOfDay());
        } else if (dateRange.equals("1year")) {
            LocalDate startDateOfPreviousYear = LocalDate.now().minusYears(1).withDayOfMonth(1);

            // Lấy ngày cuối cùng của 12 tháng trước
            LocalDate endDateOfPreviousYear = LocalDate.now().minusMonths(1).withDayOfMonth(
                    LocalDate.now().minusMonths(1).lengthOfMonth());

            startDate = Timestamp.valueOf(startDateOfPreviousYear.atStartOfDay());
            endDate = Timestamp.valueOf(endDateOfPreviousYear.atStartOfDay());
        }

        return Arrays.asList(startDate, endDate);
    }

    @Override
    public StatisticsModel getGeneralStatistics(boolean isGeneral, String dateRange, Timestamp startDate, Timestamp endDate) {
        if (isGeneral) {
            startDate = getDetailDateRange(dateRange).get(0);
            endDate = getDetailDateRange(dateRange).get(1);
        }

        long totalDocuments = getTotalDocumentCount(null, startDate, endDate);

        long totalPendingDocuments = getPendingDocumentCount(null, startDate, endDate);

        long totalUsers = getTotalUserCount(null, startDate, endDate);

        Map<Integer, Long> documentCountByMonth = getDocumentCountByMonth(null);

        Map<String, Long> documentCountByCategory = getDocumentCountByCategory(startDate, endDate, null);

        Map<String, Long> documentCountByField = getDocumentCountByField(startDate, endDate, null);

        Map<String, Long> documentCountByOrganization = getDocumentCountByOrganization(startDate, endDate);

        Map<Integer, Long> userCountByMonth = getUserCountByMonth(null);

        StatisticsModel statisticsModel = StatisticsModel.builder()
                .totalDocuments((int) totalDocuments)
                .totalPendingDocuments((int) totalPendingDocuments)
                .totalUsers((int) totalUsers)
                .documentsByMonth(documentCountByMonth)
                .documentsByCategory(documentCountByCategory)
                .documentsByField(documentCountByField)
                .documentsByOrganization(documentCountByOrganization)
                .usersByMonth(userCountByMonth)
                .build();

        return statisticsModel;
    }

    @Override
    public StatisticsModel getGeneralStatisticsForManager(boolean isGeneral, String dateRange, Timestamp startDate, Timestamp endDate) {
        if (isGeneral) {
            startDate = getDetailDateRange(dateRange).get(0);
            endDate = getDetailDateRange(dateRange).get(1);
        }

        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        long totalDocuments = getTotalDocumentCount(user.getOrganization(), startDate, endDate);

        long totalPendingDocuments = getPendingDocumentCount(user.getOrganization(), startDate, endDate);

        long totalUsers = getTotalUserCount(user.getOrganization(), startDate, endDate);

        Map<Integer, Long> documentCountByMonth = getDocumentCountByMonth(user.getOrganization());

        Map<String, Long> documentCountByCategory = getDocumentCountByCategory(startDate, endDate, user.getOrganization());

        Map<String, Long> documentCountByField = getDocumentCountByField(startDate, endDate, user.getOrganization());

        Map<Integer, Long> userCountByMonth = getUserCountByMonth(user.getOrganization());

        StatisticsModel statisticsModel = StatisticsModel.builder()
                .totalDocuments((int) totalDocuments)
                .totalPendingDocuments((int) totalPendingDocuments)
                .totalUsers((int) totalUsers)
                .documentsByMonth(documentCountByMonth)
                .documentsByCategory(documentCountByCategory)
                .documentsByField(documentCountByField)
                .usersByMonth(userCountByMonth)
                .build();

        return statisticsModel;
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

    public Map<Integer, Long> getDocumentCountByMonth(Organization organization) {
        List<Object[]> documentsByMonth = documentRepository.countDocumentsByMonth(organization);
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

    public Map<Integer, Long> getUserCountByMonth(Organization organization) {
        List<Object[]> usersByMonth = userRepositoty.countUsersByMonth(organization);
        Map<Integer, Long> userCountByMonth = usersByMonth.stream()
                .collect(Collectors.toMap(
                        result -> ((Number) result[0]).intValue(), // Tháng
                        result -> (Long) result[1] // Số lượng user
                ));

        ALL_MONTHS.forEach(month -> userCountByMonth.putIfAbsent(month, 0L));
        return userCountByMonth;
    }
}
