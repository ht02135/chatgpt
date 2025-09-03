package simple.chatgpt.validator.property;

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
@Constraint(validatedBy = PropertyValidator.class)
@Target({ FIELD, METHOD, PARAMETER, TYPE })
@Retention(RUNTIME)
public @interface ValidProperty {

    String message() default "Invalid property";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
