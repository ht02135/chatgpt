package simple.chatgpt.validator.email;

import simple.chatgpt.pojo.mybatis.Property;
import simple.chatgpt.service.mybatis.PropertyServiceImpl;
import simple.chatgpt.validator.property.PropertyValidator;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;

public class UserEmailValidator implements ConstraintValidator<UserEmail, String> {
	private static final Logger logger = LogManager.getLogger(UserEmailValidator.class);
	
    private static List<String> validDomains;

    static {
        validDomains = EmailDomainLoader.loadDomains();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        
        if (!validDomains.stream().anyMatch(value::endsWith)) {
        	return false;
        }
        
        logger.debug("#############");
        logger.debug("isValid PASS VALIDATION value: {}", value);
        logger.debug("#############");
        return true;
    }
}