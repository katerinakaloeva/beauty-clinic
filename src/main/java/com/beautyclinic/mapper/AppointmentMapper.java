package com.beautyclinic.mapper;

import com.beautyclinic.dto.AppointmentCreateDto;
import com.beautyclinic.dto.AppointmentReadDto;
import com.beautyclinic.dto.CustomerBookingDto;
import com.beautyclinic.model.Appointment;
import com.beautyclinic.model.AppointmentStatus;
import com.beautyclinic.model.Treatment;
import com.beautyclinic.model.UserAccount;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

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

    public Appointment toCustomerBookingEntity(
            CustomerBookingDto dto,
            Treatment treatment,
            UserAccount customer,
            UserAccount aesthetician,
            LocalTime endTime) {

        Appointment appointment = new Appointment();

        appointment.setCustomer(customer);
        appointment.setAesthetician(aesthetician);
        appointment.setTreatment(treatment);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStartTime(dto.getStartTime());
        appointment.setEndTime(endTime);
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