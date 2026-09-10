package com.beautyclinic.controller.api;

import com.beautyclinic.dto.TreatmentReadDto;
import com.beautyclinic.service.TreatmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/treatments")
@RequiredArgsConstructor
public class TreatmentApiController {

    private final TreatmentService treatmentService;

    @GetMapping
    public List<TreatmentReadDto> getActiveTreatments() {
        return treatmentService.getActiveTreatments();
    }

    @GetMapping("/{id}")
    public TreatmentReadDto getTreatmentById(
            @PathVariable Long id
    ) {
        return treatmentService.getTreatmentById(id);
    }
}