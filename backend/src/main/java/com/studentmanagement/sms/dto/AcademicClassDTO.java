package com.studentmanagement.sms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO pour AcademicClass. Utilisé à la fois en entrée (création/modification)
 * et en sortie (réponse API).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicClassDTO {

    private Long id;

    @NotBlank(message = "Le nom de la classe est obligatoire")
    private String name;

    @NotBlank(message = "Le niveau est obligatoire")
    private String level;

    // Champs additionnels utiles pour l'affichage (comptes), remplis par le service
    private Integer studentCount;
    private Integer subjectCount;
}
