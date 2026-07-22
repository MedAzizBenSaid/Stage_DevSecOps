package com.studentmanagement.sms.mapper;

import com.studentmanagement.sms.dto.StudentGradeDTO;
import com.studentmanagement.sms.entity.Student;
import com.studentmanagement.sms.entity.StudentGrade;
import com.studentmanagement.sms.entity.Subject;
import org.springframework.stereotype.Component;

@Component
public class StudentGradeMapper {

    public StudentGradeDTO toDTO(StudentGrade entity) {
        if (entity == null) return null;
        return StudentGradeDTO.builder()
                .id(entity.getId())
                .grade(entity.getGrade())
                .studentId(entity.getStudent() != null ? entity.getStudent().getId() : null)
                .studentFullName(entity.getStudent() != null
                        ? entity.getStudent().getFirstName() + " " + entity.getStudent().getLastName()
                        : null)
                .subjectId(entity.getSubject() != null ? entity.getSubject().getId() : null)
                .subjectName(entity.getSubject() != null ? entity.getSubject().getName() : null)
                .sessionType(entity.getSessionType())
                .build();
    }

    public StudentGrade toEntity(StudentGradeDTO dto, Student student, Subject subject) {
        if (dto == null) return null;
        return StudentGrade.builder()
                .id(dto.getId())
                .grade(dto.getGrade())
                .student(student)
                .subject(subject)
                .sessionType(dto.getSessionType())
                .build();
    }
}
