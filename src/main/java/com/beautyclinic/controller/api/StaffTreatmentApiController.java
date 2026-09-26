package com.beautyclinic.controller.api;

import com.beautyclinic.dto.TreatmentCreateDto;
import com.beautyclinic.dto.TreatmentReadDto;
import com.beautyclinic.service.TreatmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/treatments")
@RequiredArgsConstructor
public class StaffTreatmentApiController {
    private final TreatmentService treatmentService;

    @GetMapping
    public List<TreatmentReadDto> getTreatments() {
        return treatmentService.getAllTreatments();
    }

    @PostMapping
    public ResponseEntity<TreatmentReadDto> createTreatment(
            @Valid @RequestBody TreatmentCreateDto dto
    ) {
        TreatmentReadDto treatment = treatmentService.createTreatment(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(treatment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TreatmentReadDto> updateTreatment(
            @PathVariable Long id,
            @Valid @RequestBody TreatmentCreateDto dto
    ) {
        TreatmentReadDto treatment = treatmentService.updateTreatment(id, dto);

        return ResponseEntity.ok(treatment);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<TreatmentReadDto> deactivateTreatment(
            @PathVariable Long id
    ) {
        TreatmentReadDto treatment = treatmentService.deactivateTreatment(id);

        return ResponseEntity.ok(treatment);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<TreatmentReadDto> activateTreatment(
            @PathVariable Long id
    ) {
        TreatmentReadDto treatment = treatmentService.activateTreatment(id);

        return ResponseEntity.ok(treatment);
    }

}
