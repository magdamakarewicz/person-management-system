package com.enjoythecode.userservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import com.enjoythecode.userservice.validator.implementation.PasswordValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({FIELD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    String message() default "Password cannot be null; should contain at least 7 digits, " +
            "at least one upper case and one digit";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
