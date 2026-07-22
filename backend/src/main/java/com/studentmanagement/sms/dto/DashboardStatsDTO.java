package com.studentmanagement.sms.dto;

import lombok.*;

/**
 * Statistiques globales affichées sur le Dashboard.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {

    private long totalClasses;
    private long totalStudents;
    private long totalSubjects;
    private long totalAdmitted;
    private long totalControlSession;
    private long totalFailed;
}
