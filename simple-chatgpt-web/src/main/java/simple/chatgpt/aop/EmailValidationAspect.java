package simple.chatgpt.aop;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.ConstraintViolation;

import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class EmailValidationAspect {

    private final Validator validator;

    public EmailValidationAspect() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Around("execution(* simple.chatgpt.service.mybatis.*.*(..))")
    public Object validateMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg != null) {
                Set<ConstraintViolation<Object>> violations = validator.validate(arg);
                if (!violations.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (ConstraintViolation<Object> v : violations) {
                        sb.append(v.getPropertyPath())
                          .append(": ")
                          .append(v.getMessage())
                          .append("\n");
                    }
                    throw new RuntimeException("Validation failed:\n" + sb);
                }
            }
        }

        return joinPoint.proceed();
    }
}
