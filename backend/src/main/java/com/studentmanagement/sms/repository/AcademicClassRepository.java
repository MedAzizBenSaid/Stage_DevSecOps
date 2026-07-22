package com.studentmanagement.sms.repository;

import com.studentmanagement.sms.entity.AcademicClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicClassRepository extends JpaRepository<AcademicClass, Long> {

    Optional<AcademicClass> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
