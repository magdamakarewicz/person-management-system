package com.enjoythecode.dictionaryservice.exception.constraint;

import com.enjoythecode.dictionaryservice.exception.handler.ExceptionResponseBody;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DictionaryUniqueNameConstraintErrorHandler implements ConstraintErrorHandler {

    @Override
    public ExceptionResponseBody mapToErrorDto() {
        return new ExceptionResponseBody(
                List.of("Duplicated entry for 'name' field."),
                "NAME_NOT_UNIQUE",
                LocalDateTime.now()
        );
    }

    @Override
    public String getConstraintName() {
        return "UC_DICTIONARY_NAME";
    }

}
