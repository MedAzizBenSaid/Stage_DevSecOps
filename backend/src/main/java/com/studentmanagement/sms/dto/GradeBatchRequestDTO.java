package com.studentmanagement.sms.dto;

import com.studentmanagement.sms.entity.SessionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * Requête permettant d'enregistrer plusieurs notes en une seule opération
 * pour un étudiant donné (comme demandé : "toutes les notes en un seul clic").
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeBatchRequestDTO {

    @NotNull(message = "L'étudiant est obligatoire")
    private Long studentId;

    @NotNull(message = "Le type de session est obligatoire")
    private SessionType sessionType;

    @NotEmpty(message = "Au moins une note doit être fournie")
    @Valid
    private List<GradeEntryDTO> grades;
}
