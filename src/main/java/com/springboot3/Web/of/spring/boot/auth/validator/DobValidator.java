package com.springboot3.Web.of.spring.boot.auth.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DobValidator implements ConstraintValidator<DobContraint, LocalDate> {
    int min;

    @Override
    public void initialize(DobContraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) return true;
        long years = ChronoUnit.YEARS.between(value, LocalDate.now());
        return years >= min;
    }
}
