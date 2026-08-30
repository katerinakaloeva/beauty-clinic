package com.beautyclinic.validator;

import com.beautyclinic.core.exception.InvalidAppointmentTimeException;
import com.beautyclinic.dto.AppointmentCreateDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class AppointmentValidator {

    public void validate(AppointmentCreateDto dto) {

        LocalTime openingTime = LocalTime.of(9, 0);
        LocalTime closingTime = LocalTime.of(18, 0);

        if (dto.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new InvalidAppointmentTimeException(
                    "Appointment date cannot be in the past"
            );
        }

        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new InvalidAppointmentTimeException(
                    "Start time must be before end time"
            );
        }

        if (dto.getStartTime().isBefore(openingTime)
                || dto.getEndTime().isAfter(closingTime)) {

            throw new InvalidAppointmentTimeException(
                    "Appointment must be between 09:00 and 18:00"
            );
        }
    }
}