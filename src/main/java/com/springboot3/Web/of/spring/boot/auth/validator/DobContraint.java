package com.springboot3.Web.of.spring.boot.auth.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Constraint(validatedBy = {DobValidator.class})
@Target({FIELD})
@Retention(RUNTIME)
public @interface DobContraint {
    String message() default "Invalid data of birth";

    int min();

    Class<?>[] groups() default {};// Nhóm cho validation (ít dùng)

    Class<? extends Payload>[] payload() default {};
}
