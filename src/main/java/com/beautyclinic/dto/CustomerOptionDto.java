package com.beautyclinic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerOptionDto {

    private Long id;
    private String fullName;
    private String email;
}
