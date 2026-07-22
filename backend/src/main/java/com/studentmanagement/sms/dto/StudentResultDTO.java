package com.studentmanagement.sms.dto;

import com.studentmanagement.sms.entity.ResultStatus;
import lombok.*;

/**
 * Bulletin complet d'un étudiant : informations personnelles,
 * détail des deux semestres, moyenne annuelle et statut final.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResultDTO {

    private Long studentId;
    private String registrationNumber;
    private String firstName;
    private String lastName;
    private String academicClassName;

    private SemesterResultDTO semester1;
    private SemesterResultDTO semester2;

    private Double annualAverage;              // basée sur les notes MAIN uniquement
    private ResultStatus mainSessionResult;    // ADMITTED ou CONTROL_SESSION

    private Double annualAverageAfterControl;  // recalculée si session de contrôle
    private ResultStatus finalResult;          // ADMITTED ou FAILED (null si pas encore de session de contrôle nécessaire/saisie)
}
