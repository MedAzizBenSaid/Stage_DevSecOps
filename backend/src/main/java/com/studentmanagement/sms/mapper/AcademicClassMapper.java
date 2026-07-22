package com.studentmanagement.sms.mapper;

import com.studentmanagement.sms.dto.AcademicClassDTO;
import com.studentmanagement.sms.entity.AcademicClass;
import org.springframework.stereotype.Component;

/**
 * Conversion AcademicClass (Entity) <-> AcademicClassDTO.
 * Garder cette logique hors des services permet de garder les services
 * concentrés sur la logique métier uniquement.
 */
@Component
public class AcademicClassMapper {

    public AcademicClassDTO toDTO(AcademicClass entity) {
        if (entity == null) return null;
        return AcademicClassDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .level(entity.getLevel())
                .studentCount(entity.getStudents() != null ? entity.getStudents().size() : 0)
                .subjectCount(entity.getSubjects() != null ? entity.getSubjects().size() : 0)
                .build();
    }

    public AcademicClass toEntity(AcademicClassDTO dto) {
        if (dto == null) return null;
        return AcademicClass.builder()
                .id(dto.getId())
                .name(dto.getName())
                .level(dto.getLevel())
                .build();
    }
}
