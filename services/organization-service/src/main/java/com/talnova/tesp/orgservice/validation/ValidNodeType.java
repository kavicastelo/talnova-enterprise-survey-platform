package com.talnova.tesp.orgservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = NodeTypeValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ValidNodeType {

    String message() default "Unrecognized Node Type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
