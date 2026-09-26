package com.beautyclinic.service;

import com.beautyclinic.core.exception.TreatmentNotFoundException;
import com.beautyclinic.dto.TreatmentCreateDto;
import com.beautyclinic.dto.TreatmentReadDto;
import com.beautyclinic.mapper.TreatmentMapper;
import com.beautyclinic.model.Treatment;
import com.beautyclinic.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentService {
    private final TreatmentRepository treatmentRepository;
    private final TreatmentMapper treatmentMapper;


    public TreatmentReadDto  createTreatment(TreatmentCreateDto dto) {
        Treatment treatment = treatmentMapper.toEntity(dto);
        Treatment savedTreatment = treatmentRepository.save(treatment);

        return treatmentMapper.toReadDto(savedTreatment);    }

    public List<TreatmentReadDto> getAllTreatments() {

        return treatmentRepository.findAll()
                .stream()
                .map(treatmentMapper::toReadDto)
                .toList();
    }
    public List<TreatmentReadDto>  getActiveTreatments(){
        return treatmentRepository.findByActiveTrue()
                .stream()
                .map(treatmentMapper::toReadDto)
                .toList();
    }

    public TreatmentReadDto updateTreatment(
            Long id,
            TreatmentCreateDto dto
    ) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() ->
                        new TreatmentNotFoundException("Η θεραπεία δεν βρέθηκε"));

        treatmentMapper.updateEntity(dto, treatment);

        Treatment savedTreatment = treatmentRepository.save(treatment);

        return treatmentMapper.toReadDto(savedTreatment);
    }

    public TreatmentReadDto getTreatmentById(Long id) {

        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() ->
                    new TreatmentNotFoundException("Η θεραπεία δεν βρέθηκε"));

        return treatmentMapper.toReadDto(treatment);
    }

    public void deleteTreatmentById(Long id) {

        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() ->
                    new TreatmentNotFoundException("Η θεραπεία δεν βρέθηκε"));

        treatmentRepository.delete(treatment);
    }

    public TreatmentReadDto deactivateTreatment(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() ->
                        new TreatmentNotFoundException("Η θεραπεία δεν βρέθηκε"));

        treatment.setActive(false);

        Treatment savedTreatment = treatmentRepository.save(treatment);

        return treatmentMapper.toReadDto(savedTreatment);
    }

    public TreatmentReadDto activateTreatment(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() ->
                        new TreatmentNotFoundException("Η θεραπεία δεν βρέθηκε"));

        treatment.setActive(true);

        Treatment savedTreatment = treatmentRepository.save(treatment);

        return treatmentMapper.toReadDto(savedTreatment);
    }
}
