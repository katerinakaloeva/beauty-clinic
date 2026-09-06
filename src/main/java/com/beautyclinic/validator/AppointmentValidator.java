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
                    "Η ημερομηνία του ραντεβού δεν μπορεί να είναι στο παρελθόν"
            );
        }

        if (!startTime.isBefore(endTime)) {
            throw new InvalidAppointmentTimeException(
                    "Η ώρα έναρξης πρέπει να είναι πριν από την ώρα λήξης"
            );
        }

        if (startTime.isBefore(openingTime)
                || endTime.isAfter(closingTime)) {

            throw new InvalidAppointmentTimeException(
                    "Το ραντεβού πρέπει να είναι μεταξύ 09:00 και 18:00"
            );
        }

        if (startTime.getMinute() != 0) {
            throw new InvalidAppointmentTimeException(
                    "Η ώρα έναρξης πρέπει να είναι ακριβώς στην ώρα"
            );
        }
        if (appointmentDate.isEqual(LocalDate.now())
                && !startTime.isAfter(LocalTime.now())) {

            throw new InvalidAppointmentTimeException(
                    "Η ώρα του ραντεβού πρέπει να είναι στο μέλλον"
            );
        }

    }
}
