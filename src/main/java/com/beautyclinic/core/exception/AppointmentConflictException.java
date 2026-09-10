package com.beautyclinic.core.exception;

public class AppointmentConflictException extends AppException {

    private static final String DEFAULT_CODE = "APPOINTMENT_CONFLICT";

    public AppointmentConflictException(String message) {
        super(DEFAULT_CODE, message);
    }
}
