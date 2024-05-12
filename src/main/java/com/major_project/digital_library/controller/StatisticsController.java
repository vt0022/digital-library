package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.GeneralStatisticsModel;
import com.major_project.digital_library.model.YearlyStatisticsModel;
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
import java.time.Year;

@RestController
@RequestMapping("/api/v2/statistics")
public class StatisticsController {
    private final IStatisticsService statisticsService;

    @Autowired
    public StatisticsController(IStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Operation(summary = "Thống kê một vài chỉ số cơ bản")
    @GetMapping("/general/admin")
    public ResponseEntity<?> getGeneralStatisticsForAdmin(@RequestParam(defaultValue = "true") boolean isGeneral,
                                                          @RequestParam(required = false, defaultValue = "all") String dateRange,
                                                          @RequestParam(required = false) Timestamp startDate,
                                                          @RequestParam(required = false) Timestamp endDate
    ) {
        GeneralStatisticsModel generalStatisticsModel = statisticsService.getGeneralStatistics(isGeneral, dateRange, startDate, endDate);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get general statistics successfully")
                .data(generalStatisticsModel)
                .build());
    }

    @Operation(summary = "Thống kê một vài chỉ số cơ bản cho manager")
    @GetMapping("/general/manager")
    public ResponseEntity<?> getGeneralStatisticsForManager(@RequestParam(defaultValue = "true") boolean isGeneral,
                                                            @RequestParam(required = false, defaultValue = "all") String dateRange,
                                                            @RequestParam(required = false) Timestamp startDate,
                                                            @RequestParam(required = false) Timestamp endDate) {
        GeneralStatisticsModel generalStatisticsModel = statisticsService.getGeneralStatisticsForManager(isGeneral, dateRange, startDate, endDate);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get general statistics successfully")
                .data(generalStatisticsModel)
                .build());
    }

    @Operation(summary = "Thống kê phân bổ theo tháng trong một năm")
    @GetMapping("/yearly/admin")
    public ResponseEntity<?> getYearlyStatisticsForAdmin(@RequestParam(required = false) Integer year
    ) {
        if (year == null)
            year = Year.now().getValue();

        YearlyStatisticsModel yearlyStatisticsModel = statisticsService.getYearlyStatistics(year);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get yearly statistics successfully")
                .data(yearlyStatisticsModel)
                .build());
    }

    @Operation(summary = "Thống kê phân bổ theo tháng trong một năm")
    @GetMapping("/yearly/manager")
    public ResponseEntity<?> getYearlyStatisticsForManager(@RequestParam(required = false) Integer year
    ) {
        if (year == null)
            year = Year.now().getValue();

        YearlyStatisticsModel yearlyStatisticsModel = statisticsService.getYearlyStatisticsForManager(year);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get yearly statistics successfully")
                .data(yearlyStatisticsModel)
                .build());
    }
}
