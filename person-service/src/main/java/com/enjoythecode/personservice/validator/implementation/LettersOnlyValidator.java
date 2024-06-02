package com.enjoythecode.personservice.validator.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.enjoythecode.personservice.validator.LettersOnly;

public class LettersOnlyValidator implements ConstraintValidator<LettersOnly, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s != null && !s.isEmpty() && s.matches("^[a-zA-Z ]*$");
    }

}
