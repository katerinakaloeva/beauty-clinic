package com.beautyclinic.core.exception;

import com.beautyclinic.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(
        basePackages = "com.beautyclinic.controller.api"
)
public class ApiExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponse> handleAppException(
            AppException exception
    ) {
        HttpStatus status = getHttpStatus(exception.getCode());

        log.warn( "API request failed: status={}, code={}, message={}",
                status.value(),
                exception.getCode(),
                exception.getMessage()
        );

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                status.value(),
                exception.getCode(),
                exception.getMessage()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    private HttpStatus getHttpStatus(String code) {
        return switch (code) {
            case "APPOINTMENT_NOT_FOUND",
                 "TREATMENT_NOT_FOUND" -> HttpStatus.NOT_FOUND;

            case "APPOINTMENT_CONFLICT",
                 "EMAIL_ALREADY_EXISTS" -> HttpStatus.CONFLICT;

            default -> HttpStatus.BAD_REQUEST;
        };
    }
}