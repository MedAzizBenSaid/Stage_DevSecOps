package com.studentmanagement.sms.mapper;

import com.studentmanagement.sms.dto.StudentDTO;
import com.studentmanagement.sms.entity.AcademicClass;
import com.studentmanagement.sms.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentDTO toDTO(Student entity) {
        if (entity == null) return null;
        return StudentDTO.builder()
                .id(entity.getId())
                .registrationNumber(entity.getRegistrationNumber())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .academicClassId(entity.getAcademicClass() != null ? entity.getAcademicClass().getId() : null)
                .academicClassName(entity.getAcademicClass() != null ? entity.getAcademicClass().getName() : null)
                .build();
    }

    /**
     * Construit l'entité Student à partir du DTO.
     * L'AcademicClass doit être résolue et injectée par le service
     * (le mapper ne fait pas d'accès base de données).
     */
    public Student toEntity(StudentDTO dto, AcademicClass academicClass) {
        if (dto == null) return null;
        return Student.builder()
                .id(dto.getId())
                .registrationNumber(dto.getRegistrationNumber())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .academicClass(academicClass)
                .build();
    }
}
