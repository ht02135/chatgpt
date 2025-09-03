package simple.chatgpt.validator.property;

import simple.chatgpt.pojo.mybatis.Property;
import simple.chatgpt.service.mybatis.PropertyServiceImpl;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;

public class PropertyValidator implements ConstraintValidator<ValidProperty, Property> {
	private static final Logger logger = LogManager.getLogger(PropertyValidator.class);
	
    @Override
    public void initialize(ValidProperty constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Property property, ConstraintValidatorContext context) {
    	logger.debug("isValid property: {}", property);
    	
        if (property == null) {
            return false; 
        }

        String value = property.getValue();
        String type = property.getType();

        if (value == null || type == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Property type and value cannot be null")
                   .addConstraintViolation();
            return false;
        }

        switch (type.toLowerCase()) {
            case "boolean":
                if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Value must be true or false")
                           .addPropertyNode("value")
                           .addConstraintViolation();
                    return false;
                }
                break;

            case "integer":
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Value must be an integer")
                           .addPropertyNode("value")
                           .addConstraintViolation();
                    return false;
                }
                break;

            case "decimal":
                try {
                    new BigDecimal(value);
                } catch (NumberFormatException e) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Value must be a decimal number")
                           .addPropertyNode("value")
                           .addConstraintViolation();
                    return false;
                }
                break;

            case "string":
                // No extra validation
                break;

            default:
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Unknown property type: " + type)
                       .addPropertyNode("type")
                       .addConstraintViolation();
                return false;
        }

        logger.debug("#############");
        logger.debug("isValid PASS VALIDATION property: {}", property);
        logger.debug("#############");
        
        return true;
    }
}
