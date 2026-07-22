package com.studentmanagement.sms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente la note d'un étudiant dans une matière, pour une session donnée
 * (MAIN ou CONTROL). Un étudiant ne peut avoir qu'une seule note par
 * (matière, session).
 */
@Entity
@Table(
    name = "student_grades",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_grade_student_subject_session",
        columnNames = {"student_id", "subject_id", "session_type"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;
}
