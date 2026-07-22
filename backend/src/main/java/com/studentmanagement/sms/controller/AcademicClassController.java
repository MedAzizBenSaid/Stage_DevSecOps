package com.studentmanagement.sms.controller;

import com.studentmanagement.sms.dto.AcademicClassDTO;
import com.studentmanagement.sms.service.AcademicClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST pour la gestion des classes académiques.
 * Aucune logique métier ici : tout est délégué au service.
 */
@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class AcademicClassController {

    private final AcademicClassService academicClassService;

    @GetMapping
    public ResponseEntity<List<AcademicClassDTO>> getAll() {
        return ResponseEntity.ok(academicClassService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcademicClassDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(academicClassService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AcademicClassDTO> create(@Valid @RequestBody AcademicClassDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(academicClassService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcademicClassDTO> update(@PathVariable Long id, @Valid @RequestBody AcademicClassDTO dto) {
        return ResponseEntity.ok(academicClassService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        academicClassService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
