package com.studentmanagement.sms.repository;

import com.studentmanagement.sms.entity.Semester;
import com.studentmanagement.sms.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByAcademicClassId(Long academicClassId);

    List<Subject> findByAcademicClassIdAndSemester(Long academicClassId, Semester semester);

    Optional<Subject> findByNameIgnoreCaseAndSemesterAndAcademicClassId(String name, Semester semester, Long academicClassId);
}
