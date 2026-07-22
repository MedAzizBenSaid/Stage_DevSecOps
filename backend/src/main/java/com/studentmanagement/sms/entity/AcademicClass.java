package com.studentmanagement.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une classe académique (ex: GL1, GL2, RT1).
 * Une classe contient plusieurs étudiants et plusieurs matières.
 */
@Entity
@Table(name = "academic_classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String level;

    @OneToMany(mappedBy = "academicClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "academicClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Subject> subjects = new ArrayList<>();
}
