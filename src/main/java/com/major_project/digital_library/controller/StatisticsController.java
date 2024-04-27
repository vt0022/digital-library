package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.StatisticsModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IDocumentService;
import com.major_project.digital_library.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/statistics")
public class StatisticsController {
    private final IUserService userService;
    private final IDocumentService documentService;

    private static final List<Integer> ALL_MONTHS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

    @Autowired
    public StatisticsController(IUserService userService, IDocumentService documentService) {
        this.userService = userService;
        this.documentService = documentService;
    }

    @Operation(summary = "Thống kê một vài chỉ số cơ bản")
    @GetMapping("/admin")
    public ResponseEntity<?> getGeneralStatistics() {
        long totalDocuments = documentService.count();

        long totalPendingDocuments = documentService.countByVerifiedStatus(0);

        long totalUsers = userService.count();

        Map<Integer, Long> documentCountByMonth = getDocumentCountByMonth(null);

        Map<String, Long> documentCountByCategory = getDocumentCountByCategory(null);

        Map<String, Long> documentCountByField = getDocumentCountByField(null);

        Map<String, Long> documentCountByOrganization = getDocumentCountByOrganization();

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

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get general statistics successfully")
                .data(statisticsModel)
                .build());
    }

    @Operation(summary = "Thống kê một vài chỉ số cơ bản cho manager")
    @GetMapping("/manager")
    public ResponseEntity<?> getGeneralStatisticsForManager() {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));
        long totalDocuments = documentService.countByOrganization(user.getOrganization());

        long totalPendingDocuments = documentService.countByVerifiedStatusAndOrganization(0, user.getOrganization());

        long totalUsers = userService.countByOrganization(user.getOrganization());

        Map<Integer, Long> documentCountByMonth = getDocumentCountByMonth(user.getOrganization());

        Map<String, Long> documentCountByCategory = getDocumentCountByCategory(user.getOrganization());

        Map<String, Long> documentCountByField = getDocumentCountByField(user.getOrganization());

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

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get general statistics successfully")
                .data(statisticsModel)
                .build());
    }

    public Map<Integer, Long> getDocumentCountByMonth(Organization organization) {
        List<Object[]> documentsByMonth = documentService.countDocumentsByMonth(organization);
        Map<Integer, Long> documentCountByMonth = documentsByMonth.stream()
                .collect(Collectors.toMap(
                        result -> ((Number) result[0]).intValue(),
                        result -> (Long) result[1]
                ));

        ALL_MONTHS.forEach(month -> documentCountByMonth.putIfAbsent(month, 0L));
        return documentCountByMonth;
    }

    public Map<String, Long> getDocumentCountByCategory(Organization organization) {
        List<Object[]> documentsByCategory = documentService.countDocumentsByCategory(organization);
        Map<String, Long> documentCountByCategory = documentsByCategory.stream()
                .collect(Collectors.toMap(
                        result -> result[0].toString(),
                        result -> (Long) result[1]
                ));

        return documentCountByCategory;
    }

    public Map<String, Long> getDocumentCountByField(Organization organization) {
        List<Object[]> documentsByField = documentService.countDocumentsByField(organization);
        Map<String, Long> documentCountByField = documentsByField.stream()
                .collect(Collectors.toMap(
                        result -> result[0].toString(),
                        result -> (Long) result[1]
                ));

        return documentCountByField;
    }

    public Map<String, Long> getDocumentCountByOrganization() {
        List<Object[]> documentsByOrganization = documentService.countDocumentsByOrganization();
        Map<String, Long> documentCountByOrganization = documentsByOrganization.stream()
                .collect(Collectors.toMap(
                        result -> result[0].toString(),
                        result -> (Long) result[1]
                ));

        return documentCountByOrganization;
    }

    public Map<Integer, Long> getUserCountByMonth(Organization organization) {
        List<Object[]> usersByMonth = userService.countUsersByMonth(organization);
        Map<Integer, Long> userCountByMonth = usersByMonth.stream()
                .collect(Collectors.toMap(
                        result -> ((Number) result[0]).intValue(), // Tháng
                        result -> (Long) result[1] // Số lượng user
                ));

        ALL_MONTHS.forEach(month -> userCountByMonth.putIfAbsent(month, 0L));
        return userCountByMonth;
    }

}
