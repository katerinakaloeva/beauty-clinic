package com.beautyclinic.core.exception;

public class InactiveTreatmentException extends AppException {

    private static final String DEFAULT_CODE = "INACTIVE_TREATMENT";

    public InactiveTreatmentException(String message) {
        super(DEFAULT_CODE, message);
    }
}
