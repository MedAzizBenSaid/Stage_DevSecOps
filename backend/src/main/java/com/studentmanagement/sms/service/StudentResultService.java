package com.studentmanagement.sms.service;

import com.studentmanagement.sms.dto.*;
import com.studentmanagement.sms.entity.*;
import com.studentmanagement.sms.mapper.StudentMapper;
import com.studentmanagement.sms.repository.StudentGradeRepository;
import com.studentmanagement.sms.repository.StudentRepository;
import com.studentmanagement.sms.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service central de calcul des résultats académiques.
 *
 * IMPORTANT : aucun résultat (moyenne, statut) n'est jamais stocké en base.
 * Tout est recalculé à la volée à partir des notes existantes, afin de garantir
 * que le résultat reflète toujours l'état actuel des notes (pas de risque
 * d'incohérence si une note est modifiée après coup).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentResultService {

    private static final double PASSING_AVERAGE = 10.0;

    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentGradeRepository studentGradeRepository;
    private final StudentService studentService;
    private final StudentMapper studentMapper;

    /**
     * Construit le bulletin complet d'un étudiant : les deux semestres,
     * la moyenne annuelle, le résultat de session principale, et si nécessaire
     * le résultat final après session de contrôle.
     */
    public StudentResultDTO getStudentResult(Long studentId) {
        Student student = studentService.findEntityById(studentId);
        Long classId = student.getAcademicClass().getId();

        List<StudentGrade> allGrades = studentGradeRepository.findByStudentId(studentId);

        SemesterResultDTO s1 = buildSemesterResult(classId, Semester.S1, allGrades);
        SemesterResultDTO s2 = buildSemesterResult(classId, Semester.S2, allGrades);

        Double annualAverage = average(s1.getSemesterAverage(), s2.getSemesterAverage());
        ResultStatus mainSessionResult = decideResult(annualAverage, PASSING_AVERAGE, null);

        Double annualAverageAfterControl = null;
        ResultStatus finalResult = null;

        if (mainSessionResult == ResultStatus.CONTROL_SESSION) {
            annualAverageAfterControl = average(s1.getSemesterAverageAfterControl(), s2.getSemesterAverageAfterControl());
            finalResult = decideFinalResult(annualAverageAfterControl);
        } else if (mainSessionResult == ResultStatus.ADMITTED) {
            // Pas besoin de session de contrôle : le résultat final est déjà connu
            annualAverageAfterControl = annualAverage;
            finalResult = ResultStatus.ADMITTED;
        }

        return StudentResultDTO.builder()
                .studentId(student.getId())
                .registrationNumber(student.getRegistrationNumber())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .academicClassName(student.getAcademicClass().getName())
                .semester1(s1)
                .semester2(s2)
                .annualAverage(annualAverage)
                .mainSessionResult(mainSessionResult)
                .annualAverageAfterControl(annualAverageAfterControl)
                .finalResult(finalResult)
                .build();
    }

    /**
     * Retourne la liste des étudiants dont le résultat de session principale
     * est CONTROL_SESSION : ce sont les seuls étudiants qui doivent apparaître
     * dans le module "Control Session Grades".
     */
    public List<StudentDTO> getStudentsRequiringControlSession() {
        return studentRepository.findAll().stream()
                .filter(student -> {
                    StudentResultDTO result = getStudentResult(student.getId());
                    return result.getMainSessionResult() == ResultStatus.CONTROL_SESSION;
                })
                .map(studentMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ==================== Logique interne ====================

    private SemesterResultDTO buildSemesterResult(Long classId, Semester semester, List<StudentGrade> allGrades) {
        List<Subject> subjects = subjectRepository.findByAcademicClassIdAndSemester(classId, semester);

        List<SubjectGradeRowDTO> rows = subjects.stream()
                .map(subject -> buildRow(subject, allGrades))
                .collect(Collectors.toList());

        Double semesterAverage = weightedAverage(rows, false);
        Double semesterAverageAfterControl = weightedAverage(rows, true);

        return SemesterResultDTO.builder()
                .semester(semester)
                .subjectGrades(rows)
                .semesterAverage(semesterAverage)
                .semesterAverageAfterControl(semesterAverageAfterControl)
                .build();
    }

    private SubjectGradeRowDTO buildRow(Subject subject, List<StudentGrade> allGrades) {
        Double mainGrade = findGrade(allGrades, subject.getId(), SessionType.MAIN);
        Double controlGrade = findGrade(allGrades, subject.getId(), SessionType.CONTROL);

        return SubjectGradeRowDTO.builder()
                .subjectId(subject.getId())
                .subjectName(subject.getName())
                .coefficient(subject.getCoefficient())
                .mainGrade(mainGrade)
                .controlGrade(controlGrade)
                .build();
    }

    private Double findGrade(List<StudentGrade> allGrades, Long subjectId, SessionType sessionType) {
        return allGrades.stream()
                .filter(g -> g.getSubject().getId().equals(subjectId) && g.getSessionType() == sessionType)
                .map(StudentGrade::getGrade)
                .findFirst()
                .orElse(null);
    }

    /**
     * Calcule la moyenne pondérée Somme(note x coefficient) / Somme(coefficient).
     * Seules les matières pour lesquelles une note existe sont prises en compte.
     * useEffectiveGrade=true -> utilise la note de contrôle si disponible, sinon la note principale.
     * useEffectiveGrade=false -> utilise uniquement la note de session principale.
     * Retourne null si aucune note n'est disponible (calcul impossible).
     */
    private Double weightedAverage(List<SubjectGradeRowDTO> rows, boolean useEffectiveGrade) {
        double weightedSum = 0.0;
        double coefficientSum = 0.0;

        for (SubjectGradeRowDTO row : rows) {
            Double grade = useEffectiveGrade
                    ? Optional.ofNullable(row.getControlGrade()).orElse(row.getMainGrade())
                    : row.getMainGrade();

            if (grade != null) {
                weightedSum += grade * row.getCoefficient();
                coefficientSum += row.getCoefficient();
            }
        }

        if (coefficientSum == 0.0) {
            return null; // aucune note saisie pour l'instant
        }
        return round(weightedSum / coefficientSum);
    }

    private Double average(Double a, Double b) {
        if (a == null || b == null) return null;
        return round((a + b) / 2.0);
    }

    private ResultStatus decideResult(Double average, double threshold, ResultStatus fallbackIfNull) {
        if (average == null) return fallbackIfNull;
        return average >= threshold ? ResultStatus.ADMITTED : ResultStatus.CONTROL_SESSION;
    }

    private ResultStatus decideFinalResult(Double averageAfterControl) {
        if (averageAfterControl == null) return null; // notes de contrôle pas encore saisies
        return averageAfterControl >= PASSING_AVERAGE ? ResultStatus.ADMITTED : ResultStatus.FAILED;
    }

    private Double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
