package com.major_project.digital_library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YearlyStatisticsModel {
    private Map<Integer, Long> documentsByMonth;

    private Map<Integer, Long> usersByMonth;

    private Map<Integer, Long> postsByMonth;

    private Map<Integer, Long> repliesByMonth;
}
