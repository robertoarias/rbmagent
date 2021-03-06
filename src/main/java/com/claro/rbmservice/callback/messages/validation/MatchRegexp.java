package com.claro.rbmservice.callback.messages.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegexpValidator.class)
public @interface MatchRegexp {
	String regexp();
	String message() default "{}";	
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
