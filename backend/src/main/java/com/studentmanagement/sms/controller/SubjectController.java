package com.studentmanagement.sms.controller;

import com.studentmanagement.sms.dto.SubjectDTO;
import com.studentmanagement.sms.entity.Semester;
import com.studentmanagement.sms.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAll() {
        return ResponseEntity.ok(subjectService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.getById(id));
    }

    @GetMapping("/class/{academicClassId}")
    public ResponseEntity<List<SubjectDTO>> getByClass(@PathVariable Long academicClassId) {
        return ResponseEntity.ok(subjectService.getByClass(academicClassId));
    }

    @GetMapping("/class/{academicClassId}/semester/{semester}")
    public ResponseEntity<List<SubjectDTO>> getByClassAndSemester(
            @PathVariable Long academicClassId,
            @PathVariable Semester semester) {
        return ResponseEntity.ok(subjectService.getByClassAndSemester(academicClassId, semester));
    }

    @PostMapping
    public ResponseEntity<SubjectDTO> create(@Valid @RequestBody SubjectDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTO> update(@PathVariable Long id, @Valid @RequestBody SubjectDTO dto) {
        return ResponseEntity.ok(subjectService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
