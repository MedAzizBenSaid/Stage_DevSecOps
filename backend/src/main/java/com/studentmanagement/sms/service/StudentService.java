package com.studentmanagement.sms.service;

import com.studentmanagement.sms.dto.StudentDTO;
import com.studentmanagement.sms.entity.AcademicClass;
import com.studentmanagement.sms.entity.Student;
import com.studentmanagement.sms.exception.DuplicateResourceException;
import com.studentmanagement.sms.exception.ResourceNotFoundException;
import com.studentmanagement.sms.mapper.StudentMapper;
import com.studentmanagement.sms.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final AcademicClassService academicClassService;

    public List<StudentDTO> getAll() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public StudentDTO getById(Long id) {
        return studentMapper.toDTO(findEntityById(id));
    }

    public List<StudentDTO> getByClass(Long academicClassId) {
        return studentRepository.findByAcademicClassId(academicClassId).stream()
                .map(studentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public StudentDTO getByRegistrationNumber(String registrationNumber) {
        Student entity = studentRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun étudiant avec le matricule : " + registrationNumber));
        return studentMapper.toDTO(entity);
    }

    public List<StudentDTO> searchByName(String query) {
        return studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query).stream()
                .map(studentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public StudentDTO create(StudentDTO dto) {
        if (studentRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new DuplicateResourceException("Le matricule '" + dto.getRegistrationNumber() + "' existe déjà");
        }
        AcademicClass academicClass = academicClassService.findEntityById(dto.getAcademicClassId());
        Student entity = studentMapper.toEntity(dto, academicClass);
        entity.setId(null);
        Student saved = studentRepository.save(entity);
        return studentMapper.toDTO(saved);
    }

    public StudentDTO update(Long id, StudentDTO dto) {
        Student entity = findEntityById(id);

        if (!entity.getRegistrationNumber().equals(dto.getRegistrationNumber())
                && studentRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new DuplicateResourceException("Le matricule '" + dto.getRegistrationNumber() + "' existe déjà");
        }

        AcademicClass academicClass = academicClassService.findEntityById(dto.getAcademicClassId());

        entity.setRegistrationNumber(dto.getRegistrationNumber());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setAcademicClass(academicClass);

        Student saved = studentRepository.save(entity);
        return studentMapper.toDTO(saved);
    }

    public void delete(Long id) {
        Student entity = findEntityById(id);
        studentRepository.delete(entity);
    }

    public Student findEntityById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable avec l'id : " + id));
    }
}
