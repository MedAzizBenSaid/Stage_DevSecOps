package com.studentmanagement.sms.dto;

import com.studentmanagement.sms.entity.Semester;
import lombok.*;

import java.util.List;

/**
 * Résultat détaillé d'un semestre : liste des matières + moyenne du semestre.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterResultDTO {

    private Semester semester;
    private List<SubjectGradeRowDTO> subjectGrades;
    private Double semesterAverage; // basé sur les notes MAIN
    private Double semesterAverageAfterControl; // basé sur les notes effectives (control si dispo, sinon main)
}
