package com.beautyclinic.core.exception;

public class PasswordMismatchException extends AppException {

    private static final String DEFAULT_CODE = "PASSWORD_MISMATCH";

    public PasswordMismatchException(String message) {
        super(DEFAULT_CODE, message);
    }
}
