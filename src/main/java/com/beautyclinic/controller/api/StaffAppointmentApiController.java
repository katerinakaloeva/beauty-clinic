package com.beautyclinic.controller.api;

import com.beautyclinic.dto.AppointmentReadDto;
import com.beautyclinic.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/staff/appointments")
@RequiredArgsConstructor
public class StaffAppointmentApiController {

    private final AppointmentService appointmentService;

    @GetMapping
    public List<AppointmentReadDto> getAppointments() {
        return appointmentService.getAllAppointments();
    }
}