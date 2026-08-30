package com.beautyclinic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TreatmentReadDto {

    private Long id;
    private String name;
    private Integer durationMinutes;
    private Double price;
    private Boolean active;
}