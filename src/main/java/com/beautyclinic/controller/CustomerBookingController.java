package com.beautyclinic.controller;

import com.beautyclinic.dto.CustomerBookingDto;
import com.beautyclinic.service.AppointmentService;
import com.beautyclinic.service.TreatmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class CustomerBookingController {

    private final AppointmentService appointmentService;
    private final TreatmentService treatmentService;

    @GetMapping("/bookings/new")
    public String showCustomerBookingForm(Model model) {

        model.addAttribute("booking", new CustomerBookingDto());
        model.addAttribute(
                "treatments",
                treatmentService.getActiveTreatments()
        );

        return "customer-booking-form";
    }

    @PostMapping("/bookings")
    public String createCustomerBooking(
            @Valid @ModelAttribute("booking") CustomerBookingDto dto,
            BindingResult result,
            Authentication authentication,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute(
                    "treatments",
                    treatmentService.getActiveTreatments()
            );

            return "customer-booking-form";
        }

        appointmentService.createCustomerBooking(
                dto,
                authentication.getName()
        );

        return "redirect:/home";
    }

    @GetMapping("/my-bookings")
    public String showCustomerAppointments(Model model, Authentication authentication){
        model.addAttribute(
                "appointments",
                appointmentService.getCustomerAppointments(authentication.getName())
        );
        return "customer-appointments";
    }
}
