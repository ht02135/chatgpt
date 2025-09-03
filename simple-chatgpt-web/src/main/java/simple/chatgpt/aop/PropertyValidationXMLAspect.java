package simple.chatgpt.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import simple.chatgpt.pojo.mybatis.Property;
import simple.chatgpt.service.mybatis.PropertyServiceImpl;
import simple.chatgpt.util.PropertyKey;

import javax.validation.*;
import java.util.Set;

/*
In Spring AOP, you have two styles of declaring advice:

Annotation-based (using @Aspect, @Before, @Around, etc.)
→ Needs only <aop:aspectj-autoproxy/> in XML.
→ Cleaner, less verbose, what I showed earlier.

XML-based AOP configuration (no @Aspect annotation).
→ Everything is defined in applicationContext.xml.
→ More old-school, but still works fine in non-Boot setups.
*/

public class PropertyValidationXMLAspect {
	private static final Logger logger = LogManager.getLogger(PropertyValidationXMLAspect.class);

    private final Validator validator;

    public PropertyValidationXMLAspect() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    // Called before updateProperty
    public void validateProperty(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        PropertyKey key = (PropertyKey) args[0];
        String newValue = (String) args[1];

    	//1. Validate
        Property prop = new Property(key.getKey(), key.getTypeName(), newValue);
        logger.debug("#############");
        logger.debug("validateProperty prop: {}", prop);
        logger.debug("#############");

        Set<ConstraintViolation<Property>> violations = validator.validate(prop);
        if (!violations.isEmpty()) {
            // Handle validation errors
            for (ConstraintViolation<Property> violation : violations) {
            	logger.debug("Validation Error: {} for property value '{}'", violation.getMessage(), prop.getValue());
            }
            throw new IllegalArgumentException("Property validation failed.");
        }
    }
}