package com.beautyclinic.controller;

import com.beautyclinic.dto.AppointmentCreateDto;
import com.beautyclinic.service.AppointmentService;
import com.beautyclinic.service.TreatmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final TreatmentService treatmentService;

    @GetMapping("/appointments/new")
    public String showAppointmentForm(Model model) {
        model.addAttribute("appointment", new AppointmentCreateDto());
        model.addAttribute("treatments", treatmentService.getAllTreatments());

        return "appointment-form";
    }

    @PostMapping("/appointments")
    public String saveAppointment(
            @Valid @ModelAttribute("appointment") AppointmentCreateDto dto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("treatments", treatmentService.getAllTreatments());
            return "appointment-form";
        }

        appointmentService.createAppointment(dto);

        return "redirect:/appointments";
    }

    @GetMapping("/appointments")
    public String showAppointments(Model model) {
        model.addAttribute(
                "appointments",
                appointmentService.getAllAppointments()
        );

        return "appointments";
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);

        return "redirect:/appointments";
    }

    @PostMapping("/appointments/{id}/complete")
    public String completeAppointment(@PathVariable Long id) {
        appointmentService.completeAppointment(id);

        return "redirect:/appointments";
    }

    @PostMapping("/appointments/{id}/no-show")
    public String markNoShow(@PathVariable Long id) {
        appointmentService.markNoShow(id);

        return "redirect:/appointments";
    }
}