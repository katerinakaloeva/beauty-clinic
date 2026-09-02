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


    public Treatment createTreatment(TreatmentCreateDto dto) {
        Treatment treatment = treatmentMapper.toEntity(dto);
        return treatmentRepository.save(treatment);
    }

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

    public void updateTreatment(Long id, TreatmentCreateDto dto) {

        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() ->
                        new TreatmentNotFoundException("Treatment not found"));

        treatmentMapper.updateEntity(dto, treatment);

        treatmentRepository.save(treatment);
    }

    public TreatmentReadDto getTreatmentById(Long id) {

        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() ->
                        new TreatmentNotFoundException("Treatment not found"));

        return treatmentMapper.toReadDto(treatment);
    }

    public void deleteTreatmentById(Long id) {

        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() ->
                        new TreatmentNotFoundException("Treatment not found"));

        treatmentRepository.delete(treatment);
    }
}
