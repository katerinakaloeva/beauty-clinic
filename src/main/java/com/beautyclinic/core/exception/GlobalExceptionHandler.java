package com.beautyclinic.core.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppointmentConflictException.class)
    public String handleAppointmentConflict(
            AppointmentConflictException ex,
            Model model) {

        model.addAttribute("errorMessage", ex.getMessage());

        return "error";
    }

    @ExceptionHandler(InvalidAppointmentTimeException.class)
    public String handleInvalidAppointmentTime(
            InvalidAppointmentTimeException ex,
            Model model) {

        model.addAttribute("errorMessage", ex.getMessage());

        return "error";
    }

    @ExceptionHandler(InactiveTreatmentException.class)
    public String handleInactiveTreatment(
            InactiveTreatmentException ex,
            Model model) {

        model.addAttribute("errorMessage", ex.getMessage());

        return "error";
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public String handleAppointmentNotFound(
            AppointmentNotFoundException ex,
            Model model) {

        model.addAttribute("errorMessage", ex.getMessage());

        return "error";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(
            IllegalStateException ex,
            Model model) {

        model.addAttribute("errorMessage", ex.getMessage());

        return "error";
    }

    @ExceptionHandler(TreatmentNotFoundException.class)
    public String handleTreatmentNotFound(
            TreatmentNotFoundException ex,
            Model model) {

        model.addAttribute("errorMessage", ex.getMessage());

        return "error";
    }
}
