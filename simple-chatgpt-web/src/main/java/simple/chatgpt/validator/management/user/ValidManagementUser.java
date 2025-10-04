package simple.chatgpt.validator.management.user;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = ValidUserManagementValidator.class) //i want to run ValidUserValidator.class
public @interface ValidManagementUser {

    String message() default "User is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

