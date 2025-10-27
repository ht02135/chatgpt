package simple.chatgpt.validator.mybatis.user;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import simple.chatgpt.pojo.mybatis.MyBatisUserUser;

public class ValidUserValidator implements ConstraintValidator<ValidUser, MyBatisUserUser> {
	private static final Logger logger = LogManager.getLogger(ValidUserValidator.class);
	
    @Override
    public boolean isValid(MyBatisUserUser user, ConstraintValidatorContext context) {
        if (user == null) {
            return true; // you can return false if null should fail
        }

        boolean valid = true;

        // Example: cross-field or custom validation
        if (user.getName() == null || user.getName().isBlank()) {
            context.buildConstraintViolationWithTemplate("Name must not be blank")
                   .addPropertyNode("name")
                   .addConstraintViolation();
            valid = false;
        }

        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            context.buildConstraintViolationWithTemplate("Email must be valid")
                   .addPropertyNode("email")
                   .addConstraintViolation();
            valid = false;
        }

        // Required: disable default violation to use custom messages
        if (!valid) {
            context.disableDefaultConstraintViolation();
        }
        
        logger.debug("#############");
        logger.debug("isValid PASS VALIDATION user: {}", user);
        logger.debug("#############");

        return valid;
    }
}
