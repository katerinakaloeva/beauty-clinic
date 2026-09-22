package com.beautyclinic.controller.api;

import com.beautyclinic.dto.AppointmentReadDto;
import com.beautyclinic.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> completeAppointment(@PathVariable Long id) {
        appointmentService.completeAppointment(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/no-show")
    public ResponseEntity<Void> markNoShow(@PathVariable Long id) {
        appointmentService.markNoShow(id);

        return ResponseEntity.noContent().build();
    }
}