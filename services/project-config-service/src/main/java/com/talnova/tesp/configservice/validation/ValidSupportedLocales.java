package com.talnova.tesp.configservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = SupportedLocalesValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSupportedLocales {
    String message() default "Default locale must be present in supported locales list";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
