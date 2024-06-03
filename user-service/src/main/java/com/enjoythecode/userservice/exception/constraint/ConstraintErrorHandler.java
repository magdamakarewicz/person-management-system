package com.enjoythecode.userservice.exception.constraint;


import com.enjoythecode.userservice.exception.handler.ExceptionResponseBody;

public interface ConstraintErrorHandler {

    ExceptionResponseBody mapToErrorDto();

    String getConstraintName();

}