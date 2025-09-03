package simple.chatgpt.validator.email;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = UserEmailValidator.class)
@Target({ FIELD, METHOD, PARAMETER })
@Retention(RUNTIME)
public @interface UserEmail {
    String message() default "Email must be a company email";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
