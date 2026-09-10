package com.beautyclinic.core.exception;

public class TreatmentNotFoundException extends AppException {

    private static final String DEFAULT_CODE = "TREATMENT_NOT_FOUND";

    public TreatmentNotFoundException(String message) {
        super(DEFAULT_CODE, message);
    }
}
