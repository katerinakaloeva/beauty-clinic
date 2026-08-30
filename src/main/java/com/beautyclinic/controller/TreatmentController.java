package com.beautyclinic.controller;

import com.beautyclinic.dto.TreatmentCreateDto;
import com.beautyclinic.dto.TreatmentReadDto;
import com.beautyclinic.mapper.TreatmentMapper;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import com.beautyclinic.service.TreatmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class TreatmentController {
    private final TreatmentService treatmentService;
    private final TreatmentMapper treatmentMapper;

    @GetMapping("/treatments")
    public String showTreatments(Model model) {
        model.addAttribute("treatments", treatmentService.getAllTreatments());
        return "treatments";
    }

    @GetMapping("/treatments/new")
    public String showTreatmentForm(Model model){
        model.addAttribute("treatment", new TreatmentCreateDto());
        return "treatment-form";
    }

    @PostMapping("/treatments")
    public String createTreatment(
            @Valid @ModelAttribute("treatment") TreatmentCreateDto dto,
            BindingResult result) {

        if (result.hasErrors()) {
            return "treatment-form";
        }

        treatmentService.createTreatment(dto);

        return "redirect:/treatments";
    }

    @GetMapping("/treatments/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        TreatmentReadDto treatment =
                treatmentService.getTreatmentById(id);

        TreatmentCreateDto dto =
                treatmentMapper.toCreateDto(treatment);

        model.addAttribute("treatment", dto);
        model.addAttribute("treatmentId", id);

        return "treatment-form";
    }
    @PostMapping("/treatments/{id}")
    public String updateTreatment(
            @PathVariable Long id,
            @Valid @ModelAttribute("treatment") TreatmentCreateDto dto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("treatmentId", id);
            return "treatment-form";
        }

        treatmentService.updateTreatment(id, dto);

        return "redirect:/treatments";
    }
    @PostMapping("/treatments/{id}/delete")
    public String deleteTreatment(@PathVariable Long id) {

        treatmentService.deleteTreatmentById(id);

        return "redirect:/treatments";
    }
}