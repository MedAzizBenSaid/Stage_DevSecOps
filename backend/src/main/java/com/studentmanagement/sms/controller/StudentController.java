package com.studentmanagement.sms.controller;

import com.studentmanagement.sms.dto.StudentDTO;
import com.studentmanagement.sms.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAll() {
        return ResponseEntity.ok(studentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    @GetMapping("/class/{academicClassId}")
    public ResponseEntity<List<StudentDTO>> getByClass(@PathVariable Long academicClassId) {
        return ResponseEntity.ok(studentService.getByClass(academicClassId));
    }

    @GetMapping("/search/registration/{registrationNumber}")
    public ResponseEntity<StudentDTO> getByRegistrationNumber(@PathVariable String registrationNumber) {
        return ResponseEntity.ok(studentService.getByRegistrationNumber(registrationNumber));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudentDTO>> searchByName(@RequestParam String query) {
        return ResponseEntity.ok(studentService.searchByName(query));
    }

    @PostMapping
    public ResponseEntity<StudentDTO> create(@Valid @RequestBody StudentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> update(@PathVariable Long id, @Valid @RequestBody StudentDTO dto) {
        return ResponseEntity.ok(studentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
