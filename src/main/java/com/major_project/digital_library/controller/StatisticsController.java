package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.StatisticsModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequestMapping("/api/v2/statistics")
public class StatisticsController {
    private final IStatisticsService statisticsService;

    @Autowired
    public StatisticsController(IStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Operation(summary = "Thống kê một vài chỉ số cơ bản")
    @GetMapping("/admin")
    public ResponseEntity<?> getGeneralStatistics(@RequestParam(defaultValue = "true") boolean isGeneral,
                                                  @RequestParam(required = false, defaultValue = "all") String dateRange,
                                                  @RequestParam(required = false) Timestamp startDate,
                                                  @RequestParam(required = false) Timestamp endDate
    ) {
        StatisticsModel statisticsModel = statisticsService.getGeneralStatistics(isGeneral, dateRange, startDate, endDate);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get general statistics successfully")
                .data(statisticsModel)
                .build());
    }

    @Operation(summary = "Thống kê một vài chỉ số cơ bản cho manager")
    @GetMapping("/manager")
    public ResponseEntity<?> getGeneralStatisticsForManager(@RequestParam(defaultValue = "true") boolean isGeneral,
                                                            @RequestParam(required = false, defaultValue = "all") String dateRange,
                                                            @RequestParam(required = false) Timestamp startDate,
                                                            @RequestParam(required = false) Timestamp endDate) {
        StatisticsModel statisticsModel = statisticsService.getGeneralStatisticsForManager(isGeneral, dateRange, startDate, endDate);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get general statistics successfully")
                .data(statisticsModel)
                .build());
    }
}
