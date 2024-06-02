package com.enjoythecode.personservice.exception.handler;

import com.enjoythecode.personservice.exception.constraint.ConstraintErrorHandler;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@ControllerAdvice
public class ConstraintExceptionHandler {

    private Map<String, ConstraintErrorHandler> constraintErrorMapper;

    public ConstraintExceptionHandler(Set<ConstraintErrorHandler> handlers) {
        this.constraintErrorMapper = handlers.stream()
                .collect(Collectors.toMap(ConstraintErrorHandler::getConstraintName, Function.identity()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponseBody> handleConstraintViolationException(ConstraintViolationException e) {
        String constraintName = e.getConstraintName().substring(8, e.getConstraintName().indexOf(' ') - 8);
        ExceptionResponseBody body = constraintErrorMapper.get(constraintName).mapToErrorDto();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

}