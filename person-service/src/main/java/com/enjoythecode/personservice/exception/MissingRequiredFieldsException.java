package com.enjoythecode.personservice.exception;

public class MissingRequiredFieldsException extends RuntimeException {

    public MissingRequiredFieldsException(String message) {
        super(message);
    }
}
