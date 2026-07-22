package com.studentmanagement.sms.service;

import com.studentmanagement.sms.dto.DashboardStatsDTO;
import com.studentmanagement.sms.dto.StudentResultDTO;
import com.studentmanagement.sms.entity.ResultStatus;
import com.studentmanagement.sms.entity.Student;
import com.studentmanagement.sms.repository.AcademicClassRepository;
import com.studentmanagement.sms.repository.StudentRepository;
import com.studentmanagement.sms.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final AcademicClassRepository academicClassRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentResultService studentResultService;

    public DashboardStatsDTO getStats() {
        long totalClasses = academicClassRepository.count();
        long totalStudents = studentRepository.count();
        long totalSubjects = subjectRepository.count();

        long admitted = 0;
        long controlSession = 0;
        long failed = 0;

        for (Student student : studentRepository.findAll()) {
            StudentResultDTO result = studentResultService.getStudentResult(student.getId());

            if (result.getFinalResult() == ResultStatus.ADMITTED) {
                admitted++;
            } else if (result.getFinalResult() == ResultStatus.FAILED) {
                failed++;
            } else if (result.getMainSessionResult() == ResultStatus.CONTROL_SESSION) {
                controlSession++;
            }
        }

        return DashboardStatsDTO.builder()
                .totalClasses(totalClasses)
                .totalStudents(totalStudents)
                .totalSubjects(totalSubjects)
                .totalAdmitted(admitted)
                .totalControlSession(controlSession)
                .totalFailed(failed)
                .build();
    }
}
