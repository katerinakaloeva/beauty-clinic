package com.beautyclinic.core.exception;

public class InvalidAppointmentTimeException extends AppException {

    private static final String DEFAULT_CODE = "INVALID_APPOINTMENT_TIME";

    public InvalidAppointmentTimeException(String message) {
        super(DEFAULT_CODE, message);
    }
}
