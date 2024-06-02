package com.enjoythecode.dictionaryservice.exception.constraint;

import com.enjoythecode.dictionaryservice.exception.handler.ExceptionResponseBody;

public interface ConstraintErrorHandler {

    ExceptionResponseBody mapToErrorDto();

    String getConstraintName();

}