package simple.chatgpt.validator.mybatis.user;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/*
you don’t really need a ConstraintValidator if your goal 
is just to trigger validation via AOP.
///////////////////
///When the Aspect calls validator.validate(user):
1>It sees name has @NotBlank → checks the value.
2>It sees email has @UserEmail → runs that validator.
3>Any violations are returned as ConstraintViolation objects.
So all field-level annotations are triggered automatically.
*/

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = ValidUserValidator.class) //i want to run ValidUserValidator.class
public @interface ValidUser {

    String message() default "User is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

