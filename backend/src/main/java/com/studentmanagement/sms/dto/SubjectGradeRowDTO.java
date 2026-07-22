package com.studentmanagement.sms.dto;

import lombok.*;

/**
 * Une ligne du bulletin : une matière avec son coefficient,
 * sa note de session principale et sa note de contrôle (si disponible).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectGradeRowDTO {

    private Long subjectId;
    private String subjectName;
    private Double coefficient;
    private Double mainGrade;      // null si pas encore saisie
    private Double controlGrade;   // null si pas de session de contrôle nécessaire/saisie
}
