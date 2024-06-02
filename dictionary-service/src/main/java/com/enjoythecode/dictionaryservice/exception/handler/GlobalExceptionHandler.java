package com.enjoythecode.dictionaryservice.exception.handler;

import com.enjoythecode.dictionaryservice.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DictionaryNotFoundException.class, DictionaryValueNotFoundException.class})
    public ResponseEntity<ExceptionResponseBody> handleNotFoundException(RuntimeException e) {
        ExceptionResponseBody body = new ExceptionResponseBody(
                List.of(e.getMessage()),
                "NOT_FOUND",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler({DictionaryAlreadyExistsException.class, DictionaryValueAlreadyExistsException.class})
    public ResponseEntity<ExceptionResponseBody> handleAlreadyExistsException(RuntimeException e) {
        ExceptionResponseBody body = new ExceptionResponseBody(
                List.of(e.getMessage()),
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({InvalidIdException.class, InvalidEntityException.class, IllegalDictionaryStateException.class,
            IllegalDictionaryValueStateException.class})
    public ResponseEntity<ExceptionResponseBody> handleInvalidAndIllegalStateException(RuntimeException e) {
        ExceptionResponseBody body = new ExceptionResponseBody(
                List.of(e.getMessage()),
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponseBody> handleJakartaConstraintViolationException
            (jakarta.validation.ConstraintViolationException e) {
        List<String> fieldErrorsMessages = e.getConstraintViolations()
                .stream()
                .map(violation -> "property: " + violation.getPropertyPath() + " / invalid value: '"
                        + violation.getInvalidValue() + "' / message: " + violation.getMessage())
                .collect(Collectors.toList());
        ExceptionResponseBody body = new ExceptionResponseBody(
                fieldErrorsMessages,
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

}
