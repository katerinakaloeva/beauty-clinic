package com.beautyclinic.controller.api;

import com.beautyclinic.dto.AppointmentReadDto;
import com.beautyclinic.dto.StaffAppointmentCreateDto;
import com.beautyclinic.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @PostMapping
    public ResponseEntity<Void> createAppointment(
            @Valid @RequestBody StaffAppointmentCreateDto dto,
            Authentication authentication
    ) {
        appointmentService.createStaffAppointment(
                dto,
                authentication.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
