package com.beautyclinic.core.exception;

public class InvalidAppointmentStatusTransitionException extends RuntimeException {

    public InvalidAppointmentStatusTransitionException(String message) {
        super(message);
    }
}
