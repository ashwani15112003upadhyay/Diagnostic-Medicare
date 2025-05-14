package com.Diagnostic.utility;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotPastDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotPastDate {
    String message() default "Preferred date must be today or in the future";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

