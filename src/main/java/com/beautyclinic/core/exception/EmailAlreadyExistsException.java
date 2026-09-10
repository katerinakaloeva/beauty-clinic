package com.beautyclinic.core.exception;

public class EmailAlreadyExistsException extends AppException {

    private static final String DEFAULT_CODE = "EMAIL_ALREADY_EXISTS";

    public EmailAlreadyExistsException(String message) {
        super(DEFAULT_CODE, message);
    }
}
