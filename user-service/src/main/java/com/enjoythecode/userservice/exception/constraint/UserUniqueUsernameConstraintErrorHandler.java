package com.enjoythecode.userservice.exception.constraint;

import org.springframework.stereotype.Service;
import com.enjoythecode.userservice.exception.handler.ExceptionResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserUniqueUsernameConstraintErrorHandler implements ConstraintErrorHandler {

    @Override
    public ExceptionResponseBody mapToErrorDto() {
        return new ExceptionResponseBody(
                List.of("Duplicated entry for 'username' field."),
                "USERNAME_NOT_UNIQUE",
                LocalDateTime.now()
        );
    }

    @Override
    public String getConstraintName() {
        return "UC_USER_USERNAME";
    }

}
