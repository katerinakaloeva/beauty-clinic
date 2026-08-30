package com.beautyclinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TreatmentCreateDto {

    @NotBlank(message = "Το όνομα είναι υποχρεωτικό")
    private String name;

    @NotNull(message = "Η διάρκεια είναι υποχρεωτική")
    @Positive(message = "Η διάρκεια πρέπει να είναι μεγαλύτερη από 0")
    private Integer durationMinutes;

    @NotNull(message = "Η τιμή είναι υποχρεωτική")
    @PositiveOrZero(message = "Η τιμή δεν μπορεί να είναι αρνητική")
    private Double price;

    @NotNull(message = "Η κατάσταση ενεργού είναι υποχρεωτική")
    private Boolean active;
}