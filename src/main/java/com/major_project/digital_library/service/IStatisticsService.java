package com.major_project.digital_library.service;

import com.major_project.digital_library.model.GeneralStatisticsModel;
import com.major_project.digital_library.model.YearlyStatisticsModel;

import java.sql.Timestamp;

public interface IStatisticsService {
    GeneralStatisticsModel getGeneralStatistics(boolean isGeneral, String dateRange, Timestamp startDate, Timestamp endDate);

    YearlyStatisticsModel getYearlyStatistics(int year);

    GeneralStatisticsModel getGeneralStatisticsForManager(boolean isGeneral, String dateRange, Timestamp startDate, Timestamp endDate);

    YearlyStatisticsModel getYearlyStatisticsForManager(int year);
}
