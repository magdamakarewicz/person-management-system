package com.enjoythecode.personservice.validator.implementation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.enjoythecode.personservice.validator.Pesel;

import java.util.Optional;

public class PeselValidator implements ConstraintValidator<Pesel, String> {

    @Override
    public boolean isValid(String pesel, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Optional.ofNullable(pesel)
                    .filter(x -> x.matches("\\d{11}"))
                    .orElseThrow(() -> new RuntimeException("Pesel validation failed."));
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

}
