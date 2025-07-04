package io.ifyoudev.ifyoudevserver.core.v1.users.validator.annotation;

import io.ifyoudev.ifyoudevserver.core.v1.users.validator.UniqueEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {
    String message() default "이미 사용 중인 이메일입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
