package com.beautyclinic.mapper;

import com.beautyclinic.dto.AppointmentCreateDto;
import com.beautyclinic.dto.AppointmentReadDto;
import com.beautyclinic.model.Appointment;
import com.beautyclinic.model.AppointmentStatus;
import com.beautyclinic.model.Treatment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public Appointment toEntity(
            AppointmentCreateDto dto,
            Treatment treatment) {

        Appointment appointment = new Appointment();

        appointment.setTreatment(treatment);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStartTime(dto.getStartTime());
        appointment.setEndTime(dto.getEndTime());
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        return appointment;
    }

    public AppointmentReadDto toReadDto(Appointment appointment) {

        AppointmentReadDto dto = new AppointmentReadDto();

        dto.setId(appointment.getId());
        dto.setTreatmentName(appointment.getTreatment().getName());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStartTime(appointment.getStartTime());
        dto.setEndTime(appointment.getEndTime());
        dto.setStatus(appointment.getStatus());

        return dto;
    }
}