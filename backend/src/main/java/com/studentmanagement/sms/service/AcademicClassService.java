package com.studentmanagement.sms.service;

import com.studentmanagement.sms.dto.AcademicClassDTO;
import com.studentmanagement.sms.entity.AcademicClass;
import com.studentmanagement.sms.exception.DuplicateResourceException;
import com.studentmanagement.sms.exception.ResourceNotFoundException;
import com.studentmanagement.sms.mapper.AcademicClassMapper;
import com.studentmanagement.sms.repository.AcademicClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AcademicClassService {

    private final AcademicClassRepository academicClassRepository;
    private final AcademicClassMapper academicClassMapper;

    public List<AcademicClassDTO> getAll() {
        return academicClassRepository.findAll().stream()
                .map(academicClassMapper::toDTO)
                .collect(Collectors.toList());
    }

    public AcademicClassDTO getById(Long id) {
        AcademicClass entity = findEntityById(id);
        return academicClassMapper.toDTO(entity);
    }

    public AcademicClassDTO create(AcademicClassDTO dto) {
        if (academicClassRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException("Une classe avec le nom '" + dto.getName() + "' existe déjà");
        }
        AcademicClass entity = academicClassMapper.toEntity(dto);
        entity.setId(null); // sécurité : on ne laisse jamais le client forcer un ID en création
        AcademicClass saved = academicClassRepository.save(entity);
        return academicClassMapper.toDTO(saved);
    }

    public AcademicClassDTO update(Long id, AcademicClassDTO dto) {
        AcademicClass entity = findEntityById(id);

        // Si le nom change, vérifier qu'il n'entre pas en conflit avec une autre classe
        if (!entity.getName().equalsIgnoreCase(dto.getName())
                && academicClassRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException("Une classe avec le nom '" + dto.getName() + "' existe déjà");
        }

        entity.setName(dto.getName());
        entity.setLevel(dto.getLevel());
        AcademicClass saved = academicClassRepository.save(entity);
        return academicClassMapper.toDTO(saved);
    }

    public void delete(Long id) {
        AcademicClass entity = findEntityById(id);
        academicClassRepository.delete(entity);
    }

    /**
     * Méthode utilitaire interne, réutilisée par les autres services
     * (Student, Subject) pour résoudre une classe à partir de son ID.
     */
    public AcademicClass findEntityById(Long id) {
        return academicClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classe introuvable avec l'id : " + id));
    }
}
