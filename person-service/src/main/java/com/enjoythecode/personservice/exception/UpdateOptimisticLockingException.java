package com.enjoythecode.personservice.exception;

public class UpdateOptimisticLockingException extends RuntimeException {

    public UpdateOptimisticLockingException(String message) {
        super(message);
    }

}
