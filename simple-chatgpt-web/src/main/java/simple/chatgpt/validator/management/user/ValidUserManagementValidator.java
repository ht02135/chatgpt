package simple.chatgpt.validator.management.user;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.validator.management.loader.ValidationConfigLoader;
import simple.chatgpt.validator.management.rule.ValidationRule;

@Component
public class ValidUserManagementValidator implements ConstraintValidator<ValidManagementUser, UserManagementPojo> {

    private static final Logger logger = LogManager.getLogger(ValidUserManagementValidator.class);

    private final ValidationConfigLoader validationConfigLoader;

    @Autowired
    public ValidUserManagementValidator(ValidationConfigLoader validationConfigLoader) {
        this.validationConfigLoader = validationConfigLoader;
    }

    @Override
    public boolean isValid(UserManagementPojo user, ConstraintValidatorContext context) {
        if (user == null) {
            logger.warn("Validation skipped: user object is null");
            return true; // return false if null should fail
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        // --- Required field checks dynamically from POJO fields ---
        String[] requiredFields = {"userName", "userKey", "password", "firstName", "lastName", "email"};
        for (String field : requiredFields) {
            String value = getFieldValue(user, field);
            if (isBlank(value)) {
                addViolation(context, field + " cannot be blank");
                valid = false;
            }
        }

        // --- Dynamic regex validation based on ValidationConfigLoader instance ---
        Map<String, ValidationRule> rules = validationConfigLoader.getAllValidationRules();
        for (ValidationRule rule : rules.values()) {
            String fieldName = rule.getField();
            String value = getFieldValue(user, fieldName);

            if (!isBlank(value)) {
                if (!Pattern.matches(rule.getRegex(), value)) {
                    addViolation(context, rule.getError());
                    valid = false;
                } else {
                    logger.debug("Field [{}] with value [{}] passed regex validation", fieldName, value);
                }

                // Special handling for email domains
                if ("email".equals(fieldName)) {
                    List<String> validDomains = validationConfigLoader.getValidEmailDomains();
                    boolean domainOk = validDomains.stream().anyMatch(d -> value.endsWith(d));
                    if (!domainOk) {
                        addViolation(context, "Invalid email domain: " + value);
                        valid = false;
                    } else {
                        logger.debug("Email [{}] domain validated successfully", value);
                    }
                }
            }
        }

        logger.debug("#############");
        if (valid) {
            logger.debug("User [{}] PASS VALIDATION", user);
        } else {
            logger.warn("User [{}] FAILED VALIDATION", user);
        }
        logger.debug("#############");

        return valid;
    }

    private String getFieldValue(UserManagementPojo user, String fieldName) {
        try {
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getter = UserManagementPojo.class.getMethod(methodName);
            Object value = getter.invoke(user);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            logger.error("Failed to get value of field [{}]", fieldName, e);
            return null;
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        logger.error("Validation failed: {}", message);
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
