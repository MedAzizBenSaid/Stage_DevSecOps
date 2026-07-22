package com.studentmanagement.sms.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Représente une seule ligne de saisie de note dans un formulaire batch
 * (utilisé par GradeBatchRequestDTO).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeEntryDTO {

    @NotNull(message = "La matière est obligatoire")
    private Long subjectId;

    @NotNull(message = "La note est obligatoire")
    @DecimalMin(value = "0.0", message = "La note ne peut pas être négative")
    @DecimalMax(value = "20.0", message = "La note ne peut pas dépasser 20")
    private Double grade;
}
