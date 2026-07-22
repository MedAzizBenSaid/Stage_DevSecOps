package com.studentmanagement.sms.dto;

import com.studentmanagement.sms.entity.SessionType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO pour une note individuelle (StudentGrade).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGradeDTO {

    private Long id;

    @NotNull(message = "La note est obligatoire")
    @DecimalMin(value = "0.0", message = "La note ne peut pas être négative")
    @DecimalMax(value = "20.0", message = "La note ne peut pas dépasser 20")
    private Double grade;

    @NotNull(message = "L'étudiant est obligatoire")
    private Long studentId;

    private String studentFullName;

    @NotNull(message = "La matière est obligatoire")
    private Long subjectId;

    private String subjectName;

    @NotNull(message = "Le type de session est obligatoire")
    private SessionType sessionType;
}
