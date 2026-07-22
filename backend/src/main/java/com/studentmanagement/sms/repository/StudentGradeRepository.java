package com.studentmanagement.sms.repository;

import com.studentmanagement.sms.entity.SessionType;
import com.studentmanagement.sms.entity.StudentGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentGradeRepository extends JpaRepository<StudentGrade, Long> {

    List<StudentGrade> findByStudentId(Long studentId);

    List<StudentGrade> findByStudentIdAndSessionType(Long studentId, SessionType sessionType);

    Optional<StudentGrade> findByStudentIdAndSubjectIdAndSessionType(Long studentId, Long subjectId, SessionType sessionType);
}
