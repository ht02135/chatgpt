package simple.chatgpt.aop.mybatis;

import java.util.Arrays;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class UserEmailValidationAspect {
	private static final Logger logger = LogManager.getLogger(UserEmailValidationAspect.class);

    private final Validator validator;

    public UserEmailValidationAspect() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Around("execution(* simple.chatgpt.service.mybatis.MyBatisUserServiceImpl.save(..))")
    public Object validateUserEmail(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        
        logger.debug("#############");
        logger.debug("validateUserEmail args: {}", Arrays.toString(args));
        logger.debug("#############");

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
