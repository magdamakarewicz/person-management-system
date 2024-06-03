package com.enjoythecode.userservice.exception.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.enjoythecode.userservice.exception.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UserNotFoundException.class, RoleNotFoundException.class})
    public ResponseEntity<ExceptionResponseBody> handleNotFoundException(RuntimeException e) {
        ExceptionResponseBody body = new ExceptionResponseBody(
                List.of(e.getMessage()),
                "NOT_FOUND",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler({RoleAlreadyAssignedException.class})
    public ResponseEntity<ExceptionResponseBody> handleAlreadyExistsOrAssignedException(RuntimeException e) {
        ExceptionResponseBody body = new ExceptionResponseBody(
                List.of(e.getMessage()),
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({InvalidIdException.class, InvalidEntityException.class})
    public ResponseEntity<ExceptionResponseBody> handleInvalidAndIllegalStateException(RuntimeException e) {
        ExceptionResponseBody body = new ExceptionResponseBody(
                List.of(e.getMessage()),
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponseBody> handleConstraintViolationException(ConstraintViolationException e) {
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseBody> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> fieldErrorsMessages = e.getFieldErrors()
                .stream()
                .map(fe -> "field: " + fe.getField() + " / rejectedValue: '" + fe.getRejectedValue() +
                        "' / message: " + fe.getDefaultMessage())
                .collect(Collectors.toList());
        ExceptionResponseBody body = new ExceptionResponseBody(
                fieldErrorsMessages,
                "BAD_REQUEST",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

}
