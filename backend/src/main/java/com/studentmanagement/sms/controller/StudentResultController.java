package com.studentmanagement.sms.controller;

import com.studentmanagement.sms.dto.StudentDTO;
import com.studentmanagement.sms.dto.StudentResultDTO;
import com.studentmanagement.sms.service.StudentResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class StudentResultController {

    private final StudentResultService studentResultService;

    /**
     * Retourne le bulletin complet d'un étudiant : semestres, moyennes,
     * résultat de session principale et résultat final.
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResultDTO> getStudentResult(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentResultService.getStudentResult(studentId));
    }

    /**
     * Retourne uniquement les étudiants devant passer la session de contrôle.
     * Utilisé par la page "Control Session Grades" du frontend.
     */
    @GetMapping("/control-session-students")
    public ResponseEntity<List<StudentDTO>> getStudentsRequiringControlSession() {
        return ResponseEntity.ok(studentResultService.getStudentsRequiringControlSession());
    }
}
