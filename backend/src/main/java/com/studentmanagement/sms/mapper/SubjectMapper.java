package com.studentmanagement.sms.mapper;

import com.studentmanagement.sms.dto.SubjectDTO;
import com.studentmanagement.sms.entity.AcademicClass;
import com.studentmanagement.sms.entity.Subject;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {

    public SubjectDTO toDTO(Subject entity) {
        if (entity == null) return null;
        return SubjectDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .coefficient(entity.getCoefficient())
                .semester(entity.getSemester())
                .academicClassId(entity.getAcademicClass() != null ? entity.getAcademicClass().getId() : null)
                .academicClassName(entity.getAcademicClass() != null ? entity.getAcademicClass().getName() : null)
                .build();
    }

    public Subject toEntity(SubjectDTO dto, AcademicClass academicClass) {
        if (dto == null) return null;
        return Subject.builder()
                .id(dto.getId())
                .name(dto.getName())
                .coefficient(dto.getCoefficient())
                .semester(dto.getSemester())
                .academicClass(academicClass)
                .build();
    }
}
