package com.enjoythecode.personservice.exception;

public class PositionNotBelongToEmployeeException extends RuntimeException {

    public PositionNotBelongToEmployeeException(String message) {
        super(message);
    }

}
