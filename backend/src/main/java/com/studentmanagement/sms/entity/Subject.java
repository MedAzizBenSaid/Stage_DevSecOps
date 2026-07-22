package com.studentmanagement.sms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente une matière. Chaque matière appartient à une classe et un semestre.
 * Une matière ne peut pas exister deux fois dans la même classe et le même semestre.
 */
@Entity
@Table(
    name = "subjects",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_subject_name_semester_class",
        columnNames = {"name", "semester", "academic_class_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double coefficient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Semester semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_class_id", nullable = false)
    @ToString.Exclude
    private AcademicClass academicClass;
}
