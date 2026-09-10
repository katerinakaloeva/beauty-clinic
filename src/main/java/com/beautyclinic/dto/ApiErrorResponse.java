package com.beautyclinic.dto;

public record ApiErrorResponse(
        int status,
        String code,
        String message
) {
}