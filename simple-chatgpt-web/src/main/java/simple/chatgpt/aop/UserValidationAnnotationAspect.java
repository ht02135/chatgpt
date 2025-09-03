package simple.chatgpt.aop;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.ConstraintViolation;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import simple.chatgpt.validator.user.ValidUser;

@Aspect
@Component
public class UserValidationAnnotationAspect {
	private static final Logger logger = LogManager.getLogger(UserValidationAnnotationAspect.class);

    private final Validator validator;

    public UserValidationAnnotationAspect() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Around("execution(* simple.chatgpt.service.mybatis.*.*(..))")
    public Object validateUser(ProceedingJoinPoint joinPoint) throws Throwable {
        for (Object arg : joinPoint.getArgs()) {
            logger.debug("#############");
            logger.debug("validateUser arg: {}", arg);
            logger.debug("#############");
            
            if (arg != null && arg.getClass().isAnnotationPresent(ValidUser.class)) {
                Set<ConstraintViolation<Object>> violations = validator.validate(arg);
                if (!violations.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (ConstraintViolation<Object> v : violations) {
                        sb.append(v.getPropertyPath())
                          .append(": ")
                          .append(v.getMessage())
                          .append("\n");
                    }
                    throw new IllegalArgumentException("User validation failed:\n" + sb);
                }
            }
        }
        return joinPoint.proceed();
    }
}
