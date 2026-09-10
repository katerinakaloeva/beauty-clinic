package com.beautyclinic.core.exception;

public class AppointmentNotFoundException extends AppException {

    private static final String DEFAULT_CODE = "APPOINTMENT_NOT_FOUND";

    public AppointmentNotFoundException(String message) {
        super(DEFAULT_CODE, message);
    }
}
