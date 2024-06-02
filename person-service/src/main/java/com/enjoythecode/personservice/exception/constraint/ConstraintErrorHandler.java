package com.enjoythecode.personservice.exception.constraint;

import com.enjoythecode.personservice.exception.handler.ExceptionResponseBody;

public interface ConstraintErrorHandler {

    ExceptionResponseBody mapToErrorDto();

    String getConstraintName();

}