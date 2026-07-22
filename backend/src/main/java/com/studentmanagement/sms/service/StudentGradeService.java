package com.studentmanagement.sms.service;

import com.studentmanagement.sms.dto.GradeBatchRequestDTO;
import com.studentmanagement.sms.dto.GradeEntryDTO;
import com.studentmanagement.sms.dto.StudentGradeDTO;
import com.studentmanagement.sms.entity.SessionType;
import com.studentmanagement.sms.entity.Student;
import com.studentmanagement.sms.entity.StudentGrade;
import com.studentmanagement.sms.entity.Subject;
import com.studentmanagement.sms.exception.ResourceNotFoundException;
import com.studentmanagement.sms.mapper.StudentGradeMapper;
import com.studentmanagement.sms.repository.StudentGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentGradeService {

    private final StudentGradeRepository studentGradeRepository;
    private final StudentGradeMapper studentGradeMapper;
    private final StudentService studentService;
    private final SubjectService subjectService;

    public List<StudentGradeDTO> getByStudent(Long studentId) {
        return studentGradeRepository.findByStudentId(studentId).stream()
                .map(studentGradeMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Enregistre (ou met à jour) un lot de notes pour un étudiant, dans une
     * session donnée (MAIN ou CONTROL), en une seule opération transactionnelle.
     *
     * Comportement "upsert" : si une note existe déjà pour (étudiant, matière, session),
     * elle est mise à jour ; sinon elle est créée. Cela permet à l'admin de re-soumettre
     * le même formulaire pour corriger une note sans provoquer d'erreur de doublon.
     */
    public List<StudentGradeDTO> saveBatch(GradeBatchRequestDTO request) {
        Student student = studentService.findEntityById(request.getStudentId());

        return request.getGrades().stream()
                .map(entry -> upsertGrade(student, entry, request.getSessionType()))
                .map(studentGradeMapper::toDTO)
                .collect(Collectors.toList());
    }

    private StudentGrade upsertGrade(Student student, GradeEntryDTO entry, SessionType sessionType) {
        Subject subject = subjectService.findEntityById(entry.getSubjectId());

        StudentGrade grade = studentGradeRepository
                .findByStudentIdAndSubjectIdAndSessionType(student.getId(), subject.getId(), sessionType)
                .orElseGet(() -> StudentGrade.builder()
                        .student(student)
                        .subject(subject)
                        .sessionType(sessionType)
                        .build());

        grade.setGrade(entry.getGrade());
        return studentGradeRepository.save(grade);
    }

    public StudentGradeDTO update(Long id, StudentGradeDTO dto) {
        StudentGrade entity = findEntityById(id);
        entity.setGrade(dto.getGrade());
        StudentGrade saved = studentGradeRepository.save(entity);
        return studentGradeMapper.toDTO(saved);
    }

    public void delete(Long id) {
        StudentGrade entity = findEntityById(id);
        studentGradeRepository.delete(entity);
    }

    public StudentGrade findEntityById(Long id) {
        return studentGradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note introuvable avec l'id : " + id));
    }
}
