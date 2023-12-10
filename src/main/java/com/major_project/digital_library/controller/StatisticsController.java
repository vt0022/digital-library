package com.major_project.digital_library.controller;

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

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {
    private final IUserService userService;
    private final IDocumentService documentService;

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

        StatisticsModel statisticsModel = StatisticsModel.builder()
                .totalDocuments((int) totalDocuments)
                .totalPendingDocuments((int) totalPendingDocuments)
                .totalUsers((int) totalUsers)
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

        StatisticsModel statisticsModel = StatisticsModel.builder()
                .totalDocuments((int) totalDocuments)
                .totalPendingDocuments((int) totalPendingDocuments)
                .totalUsers((int) totalUsers)
                .build();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get general statistics successfully")
                .data(statisticsModel)
                .build());
    }
}
