package com.major_project.digital_library.service;

import com.major_project.digital_library.model.StatisticsModel;

import java.sql.Timestamp;

public interface IStatisticsService {
    StatisticsModel getGeneralStatistics(boolean isGeneral, String dateRange, Timestamp startDate, Timestamp endDate);

    StatisticsModel getGeneralStatisticsForManager(boolean isGeneral, String dateRange, Timestamp startDate, Timestamp endDate);
}
