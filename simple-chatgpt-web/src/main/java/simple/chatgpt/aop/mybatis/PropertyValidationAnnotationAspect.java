package simple.chatgpt.aop.mybatis;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import simple.chatgpt.pojo.mybatis.MyBatisProperty;
import simple.chatgpt.util.PropertyKey;

/*
In Spring AOP, you have two styles of declaring advice:

Annotation-based (using @Aspect, @Before, @Around, etc.)
→ Needs only <aop:aspectj-autoproxy/> in XML.
→ Cleaner, less verbose, what I showed earlier.

XML-based AOP configuration (no @Aspect annotation).
→ Everything is defined in applicationContext.xml.
→ More old-school, but still works fine in non-Boot setups.
*/

@Aspect
@Component
public class PropertyValidationAnnotationAspect {
	private static final Logger logger = LogManager.getLogger(PropertyValidationAnnotationAspect.class);

    private final Validator validator;

    public PropertyValidationAnnotationAspect() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    // Pointcut matching your service method
    @Pointcut("execution(* simple.chatgpt.service.mybatis.PropertyServiceImpl.updateProperty(..))")
    public void updatePropertyPointcut() {}

    // Advice before the method runs
    @Before("updatePropertyPointcut() && args(key, newValue)")
    public void validateProperty(PropertyKey key, String newValue) {
    	//1. Validate
        MyBatisProperty prop = new MyBatisProperty(key.getKey(), key.getTypeName(), newValue);
        logger.debug("#############");
        logger.debug("validateProperty prop: {}", prop);
        logger.debug("#############");

        Set<ConstraintViolation<MyBatisProperty>> violations = validator.validate(prop);
        if (!violations.isEmpty()) {
            // Handle validation errors
            for (ConstraintViolation<MyBatisProperty> violation : violations) {
            	logger.debug("Validation Error: {} for property value '{}'", violation.getMessage(), prop.getValue());
            }
            throw new IllegalArgumentException("Property validation failed.");
        }
    }
}