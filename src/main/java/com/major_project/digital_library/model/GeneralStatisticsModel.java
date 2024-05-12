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
public class GeneralStatisticsModel {
    private int totalDocuments;

    private int totalUsers;

    private int totalPendingDocuments;

    private int totalCategories;

    private int totalFields;

    private int totalOrganizations;

    private int totalPosts;

    private int totalReplies;

    private int totalSubsections;

    private int totalLabels;

    private Map<String, Long> documentsByCategory;

    private Map<String, Long> documentsByField;

    private Map<String, Long> documentsByOrganization;

    private Map<String, Long> usersByOrganization;

    private Map<String, Long> postsBySubsection;

    private Map<String, Long> postsByLabel;

    private Map<String, Long> repliesBySubsection;

    private Map<String, Long> repliesByLabel;
}
