package com.beautyclinic.validator;

import com.beautyclinic.core.exception.InvalidAppointmentTimeException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class AppointmentValidator {

    public void validate(
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime) {

        LocalTime openingTime = LocalTime.of(9, 0);
        LocalTime closingTime = LocalTime.of(18, 0);

        if (appointmentDate.isBefore(LocalDate.now())) {
            throw new InvalidAppointmentTimeException(
                    "Appointment date cannot be in the past"
            );
        }

        if (!startTime.isBefore(endTime)) {
            throw new InvalidAppointmentTimeException(
                    "Start time must be before end time"
            );
        }

        if (startTime.isBefore(openingTime)
                || endTime.isAfter(closingTime)) {

            throw new InvalidAppointmentTimeException(
                    "Appointment must be between 09:00 and 18:00"
            );
        }

        if (startTime.getMinute() != 0) {
            throw new InvalidAppointmentTimeException(
                    "Η ώρα έναρξης πρέπει να είναι ακριβώς στην ώρα"
            );
        }

    }
}