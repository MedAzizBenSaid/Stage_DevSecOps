package com.studentmanagement.sms.dto;

import com.studentmanagement.sms.entity.Semester;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO pour Subject.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDTO {

    private Long id;

    @NotBlank(message = "Le nom de la matière est obligatoire")
    private String name;

    @NotNull(message = "Le coefficient est obligatoire")
    @DecimalMin(value = "0.01", message = "Le coefficient doit être supérieur à zéro")
    private Double coefficient;

    @NotNull(message = "Le semestre est obligatoire")
    private Semester semester;

    @NotNull(message = "La classe est obligatoire")
    private Long academicClassId;

    private String academicClassName;
}
