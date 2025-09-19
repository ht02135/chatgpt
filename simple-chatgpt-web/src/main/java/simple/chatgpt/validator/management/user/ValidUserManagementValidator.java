package simple.chatgpt.validator.management.user;

import java.util.List;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.validator.management.loader.ValidationConfigLoader;
import simple.chatgpt.validator.management.rule.ValidationRule;

public class ValidUserManagementValidator implements ConstraintValidator<ValidManagementUser, UserManagementPojo> {
    private static final Logger logger = LogManager.getLogger(ValidUserManagementValidator.class);

    @Override
    public boolean isValid(UserManagementPojo user, ConstraintValidatorContext context) {
        if (user == null) {
            logger.warn("Validation skipped: user object is null");
            return true; // decide false if null should fail validation
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        // --- Required field checks ---
        if (isBlank(user.getUserName())) {
            addViolation(context, "UserName cannot be blank");
            valid = false;
        }
        if (isBlank(user.getUserKey())) {
            addViolation(context, "UserKey cannot be blank");
            valid = false;
        }
        if (isBlank(user.getPassword())) {
            addViolation(context, "Password cannot be blank");
            valid = false;
        }
        if (isBlank(user.getFirstName())) {
            addViolation(context, "FirstName cannot be blank");
            valid = false;
        }
        if (isBlank(user.getLastName())) {
            addViolation(context, "LastName cannot be blank");
            valid = false;
        }
        if (isBlank(user.getEmail())) {
            addViolation(context, "Email cannot be blank");
            valid = false;
        }

        // --- Regex validations ---
        if (!isBlank(user.getEmail())) {
            ValidationRule emailRule = ValidationConfigLoader.getValidationRule("emailValidation");
            if (!Pattern.matches(emailRule.getRegex(), user.getEmail())) {
                addViolation(context, emailRule.getError());
                valid = false;
            } else {
                logger.debug("Email [{}] passed regex validation", user.getEmail());
            }

            // --- Email domain validation ---
            List<String> validDomains = ValidationConfigLoader.getValidEmailDomains();
            boolean domainOk = validDomains.stream().anyMatch(d -> user.getEmail().endsWith(d));
            if (!domainOk) {
                addViolation(context, "Invalid email domain: " + user.getEmail());
                valid = false;
            } else {
                logger.debug("Email [{}] domain validated successfully", user.getEmail());
            }
        }

        if (!isBlank(user.getPassword())) {
            ValidationRule pwdRule = ValidationConfigLoader.getValidationRule("passwordValidation");
            if (!Pattern.matches(pwdRule.getRegex(), user.getPassword())) {
                addViolation(context, pwdRule.getError());
                valid = false;
            } else {
                logger.debug("Password passed regex validation");
            }
        }

        if (!isBlank(user.getPostCode())) {
            ValidationRule postalRule = ValidationConfigLoader.getValidationRule("postalCodeValidation");
            if (!Pattern.matches(postalRule.getRegex(), user.getPostCode())) {
                addViolation(context, postalRule.getError());
                valid = false;
            } else {
                logger.debug("PostCode [{}] passed regex validation", user.getPostCode());
            }
        }

        // --- Logging final result ---
        logger.debug("#############");
        if (valid) {
            logger.debug("User [{}] PASS VALIDATION user {}", user);
        } else {
            logger.warn("User [{}] FAILED VALIDATION user {}", user);
        }
        logger.debug("#############");

        return valid;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        logger.error("Validation failed: {}", message);
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
