package com.studentmanagement.sms.controller;

import com.studentmanagement.sms.dto.GradeBatchRequestDTO;
import com.studentmanagement.sms.dto.StudentGradeDTO;
import com.studentmanagement.sms.service.StudentGradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class StudentGradeController {

    private final StudentGradeService studentGradeService;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentGradeDTO>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentGradeService.getByStudent(studentId));
    }

    /**
     * Enregistre toutes les notes d'un étudiant en une seule opération.
     * Le champ "sessionType" dans le corps de la requête détermine si ce sont
     * des notes de session principale (MAIN) ou de contrôle (CONTROL).
     */
    @PostMapping("/batch")
    public ResponseEntity<List<StudentGradeDTO>> saveBatch(@Valid @RequestBody GradeBatchRequestDTO request) {
        return ResponseEntity.ok(studentGradeService.saveBatch(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentGradeDTO> update(@PathVariable Long id, @Valid @RequestBody StudentGradeDTO dto) {
        return ResponseEntity.ok(studentGradeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentGradeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
