package com.studentmanagement.sms.service;

import com.studentmanagement.sms.dto.SubjectDTO;
import com.studentmanagement.sms.entity.AcademicClass;
import com.studentmanagement.sms.entity.Semester;
import com.studentmanagement.sms.entity.Subject;
import com.studentmanagement.sms.exception.DuplicateResourceException;
import com.studentmanagement.sms.exception.ResourceNotFoundException;
import com.studentmanagement.sms.mapper.SubjectMapper;
import com.studentmanagement.sms.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;
    private final AcademicClassService academicClassService;

    public List<SubjectDTO> getAll() {
        return subjectRepository.findAll().stream()
                .map(subjectMapper::toDTO)
                .collect(Collectors.toList());
    }

    public SubjectDTO getById(Long id) {
        return subjectMapper.toDTO(findEntityById(id));
    }

    public List<SubjectDTO> getByClass(Long academicClassId) {
        return subjectRepository.findByAcademicClassId(academicClassId).stream()
                .map(subjectMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<SubjectDTO> getByClassAndSemester(Long academicClassId, Semester semester) {
        return subjectRepository.findByAcademicClassIdAndSemester(academicClassId, semester).stream()
                .map(subjectMapper::toDTO)
                .collect(Collectors.toList());
    }

    public SubjectDTO create(SubjectDTO dto) {
        AcademicClass academicClass = academicClassService.findEntityById(dto.getAcademicClassId());
        checkDuplicate(dto, null);
        Subject entity = subjectMapper.toEntity(dto, academicClass);
        entity.setId(null);
        Subject saved = subjectRepository.save(entity);
        return subjectMapper.toDTO(saved);
    }

    public SubjectDTO update(Long id, SubjectDTO dto) {
        Subject entity = findEntityById(id);
        AcademicClass academicClass = academicClassService.findEntityById(dto.getAcademicClassId());
        checkDuplicate(dto, id);

        entity.setName(dto.getName());
        entity.setCoefficient(dto.getCoefficient());
        entity.setSemester(dto.getSemester());
        entity.setAcademicClass(academicClass);

        Subject saved = subjectRepository.save(entity);
        return subjectMapper.toDTO(saved);
    }

    public void delete(Long id) {
        Subject entity = findEntityById(id);
        subjectRepository.delete(entity);
    }

    public Subject findEntityById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matière introuvable avec l'id : " + id));
    }

    /**
     * Vérifie qu'une matière du même nom n'existe pas déjà dans la même classe
     * et le même semestre. excludeId permet d'ignorer la matière courante lors d'une modification.
     */
    private void checkDuplicate(SubjectDTO dto, Long excludeId) {
        subjectRepository.findByNameIgnoreCaseAndSemesterAndAcademicClassId(
                dto.getName(), dto.getSemester(), dto.getAcademicClassId()
        ).filter(existing -> !existing.getId().equals(excludeId))
         .ifPresent(existing -> {
             throw new DuplicateResourceException(
                 "La matière '" + dto.getName() + "' existe déjà pour cette classe et ce semestre");
         });
    }
}
