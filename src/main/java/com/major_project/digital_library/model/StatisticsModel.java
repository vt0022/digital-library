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
public class StatisticsModel {
    private int totalDocuments;

    private int totalUsers;

    private int totalPendingDocuments;

    Map<Integer, Long> documentsByMonth;

    Map<Integer, Long> usersByMonth;

    Map<String, Long> documentsByCategory;

    Map<String, Long> documentsByField;

    Map<String, Long> documentsByOrganization;
}
