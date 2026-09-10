package com.beautyclinic.core.exception;

public class InvalidAppointmentStatusTransitionException extends AppException {

    private static final String DEFAULT_CODE = "INVALID_APPOINTMENT_STATUS_TRANSITION";

    public InvalidAppointmentStatusTransitionException(String message) {
        super(DEFAULT_CODE, message);
    }
}
