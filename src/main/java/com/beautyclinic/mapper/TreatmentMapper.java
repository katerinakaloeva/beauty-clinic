package com.beautyclinic.mapper;


import com.beautyclinic.dto.TreatmentCreateDto;
import com.beautyclinic.dto.TreatmentReadDto;
import com.beautyclinic.model.Treatment;
import org.springframework.stereotype.Component;

@Component

public class TreatmentMapper {
    public Treatment toEntity(TreatmentCreateDto dto) {
        Treatment treatment = new Treatment();

        treatment.setName(dto.getName());
        treatment.setDurationMinutes(dto.getDurationMinutes());
        treatment.setPrice(dto.getPrice());
        treatment.setActive(dto.getActive());

        return treatment;


    }

    public TreatmentReadDto toReadDto(Treatment treatment) {

        TreatmentReadDto dto = new TreatmentReadDto();

        dto.setId(treatment.getId());
        dto.setName(treatment.getName());
        dto.setDurationMinutes(treatment.getDurationMinutes());
        dto.setPrice(treatment.getPrice());
        dto.setActive(treatment.getActive());

        return dto;
    }

    public void updateEntity(TreatmentCreateDto dto, Treatment treatment) {
        treatment.setName(dto.getName());
        treatment.setDurationMinutes(dto.getDurationMinutes());
        treatment.setPrice(dto.getPrice());
        treatment.setActive(dto.getActive());
    }

    public TreatmentCreateDto toCreateDto(TreatmentReadDto treatment) {

        TreatmentCreateDto dto = new TreatmentCreateDto();

        dto.setName(treatment.getName());
        dto.setDurationMinutes(treatment.getDurationMinutes());
        dto.setPrice(treatment.getPrice());
        dto.setActive(treatment.getActive());

        return dto;
    }
    }
