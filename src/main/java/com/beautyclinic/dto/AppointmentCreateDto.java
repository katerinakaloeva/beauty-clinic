package com.beautyclinic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentCreateDto {

    @NotNull(message = "Η θεραπεία είναι υποχρεωτική")
    private Long treatmentId;

    @NotNull(message = "Η ημερομηνία είναι υποχρεωτική")
    private LocalDate appointmentDate;

    @NotNull(message = "Η ώρα έναρξης είναι υποχρεωτική")
    private LocalTime startTime;

    @NotNull(message = "Η ώρα λήξης είναι υποχρεωτική")
    private LocalTime endTime;
}
