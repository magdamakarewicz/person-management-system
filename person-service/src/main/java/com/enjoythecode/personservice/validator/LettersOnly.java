package com.enjoythecode.personservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import com.enjoythecode.personservice.validator.implementation.LettersOnlyValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Constraint(validatedBy = LettersOnlyValidator.class)
@Target({FIELD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LettersOnly {

    String message() default "Field cannot be null; can contain only letters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
