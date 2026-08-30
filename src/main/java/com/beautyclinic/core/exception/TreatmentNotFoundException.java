package com.beautyclinic.core.exception;

public class TreatmentNotFoundException extends RuntimeException {

    public TreatmentNotFoundException(String message) {
        super(message);
    }
}