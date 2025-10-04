package simple.chatgpt.validator.mybatis.email;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ValidUserEmailValidator implements ConstraintValidator<ValidUserEmail, String> {
	private static final Logger logger = LogManager.getLogger(ValidUserEmailValidator.class);
	
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