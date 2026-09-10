package com.beautyclinic.controller.api;

import com.beautyclinic.dto.AppointmentReadDto;
import com.beautyclinic.dto.CustomerBookingDto;
import com.beautyclinic.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class CustomerBookingApiController {

    private final AppointmentService appointmentService;

    @GetMapping("/available-times")
    public List<LocalTime> getAvailableStartTimes(
            @RequestParam Long treatmentId,
            @RequestParam LocalDate appointmentDate
    ) {
        return appointmentService.getAvailableStartTimes(
                treatmentId,
                appointmentDate
        );
    }

    @GetMapping("/my")
    public List<AppointmentReadDto> getMyBookings(
            Authentication authentication
    ) {
        return appointmentService.getCustomerAppointments(
                authentication.getName()
        );
    }

    @PostMapping
    public ResponseEntity<AppointmentReadDto> createCustomerBooking(
            @Valid @RequestBody CustomerBookingDto dto,
            Authentication authentication
    ) {
        AppointmentReadDto createdBooking =
                appointmentService.createCustomerBooking(
                        dto,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdBooking);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelMyBooking(
            @PathVariable Long id,
            Authentication authentication
    ) {
        appointmentService.cancelCustomerAppointment(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}
