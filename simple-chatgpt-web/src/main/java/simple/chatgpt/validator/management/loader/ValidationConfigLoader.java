package simple.chatgpt.validator.management.loader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import simple.chatgpt.service.management.PropertyManagementService;
import simple.chatgpt.util.PropertyKey;
import simple.chatgpt.validator.management.rule.ValidationRule;

@Component
public class ValidationConfigLoader {

    private static final Logger logger = LogManager.getLogger(ValidationConfigLoader.class);

    private final PropertyManagementService propertyService;

    private String configFilePath;

    private final Map<String, ValidationRule> validationRules = new HashMap<>();
    private final List<String> validEmailDomains = new ArrayList<>();
    private boolean initialized = false;

    private static final String DEFAULT_CONFIG_FILE = "/config/management/validation-config.xml";

    public ValidationConfigLoader(PropertyManagementService propertyService) {
        this.propertyService = propertyService;
    }

    @PostConstruct
    private void init() {
    	
        logger.debug("ValidationConfigLoader init: #############");
        configFilePath = DEFAULT_CONFIG_FILE;
        try {
            configFilePath = propertyService.getString(PropertyKey.VALIDATION_CONFIG_RELATIVE_PATH);
            logger.debug("ValidationConfigLoader: Loaded property VALIDATION_CONFIG_RELATIVE_PATH={}", configFilePath);
        } catch (Exception e) {
            logger.error("ValidationConfigLoader: Failed to fetch VALIDATION_CONFIG_RELATIVE_PATH, using default {}", configFilePath, e);
        }
        logger.debug("init: configFilePath={}", configFilePath);
        logger.debug("ValidationConfigLoader init: #############");

        try (InputStream inputStream = ValidationConfigLoader.class.getResourceAsStream(configFilePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find validation config: " + configFilePath);
            }

            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(inputStream);

            // --- Load <validation> rules ---
            NodeList validationNodes = document.getElementsByTagName("validation");
            for (int i = 0; i < validationNodes.getLength(); i++) {
                Element validationElement = (Element) validationNodes.item(i);
                String id = validationElement.getAttribute("id");
                String field = validationElement.getAttribute("field");
                String regex = validationElement.getAttribute("regex");
                String error = validationElement.getAttribute("error");

                ValidationRule rule = new ValidationRule(id, field, regex, error);
                validationRules.put(id, rule);
                logger.debug("Loaded validation rule: {}", rule);
            }

            // --- Load <validEmailDomains> ---
            NodeList domainNodes = document.getElementsByTagName("domain");
            for (int i = 0; i < domainNodes.getLength(); i++) {
                Element domainElement = (Element) domainNodes.item(i);
                String domainName = domainElement.getAttribute("domainName").trim();
                if (!domainName.isEmpty()) {
                    validEmailDomains.add(domainName);
                    logger.debug("Loaded valid email domain: {}", domainName);
                }
            }

            logger.info("Final validation rules loaded: {}", validationRules.keySet());
            logger.info("Final list of valid email domains: {}", validEmailDomains);

            initialized = true;

        } catch (Exception e) {
            logger.error("Failed to load validation configuration from {}", configFilePath, e);
            throw new RuntimeException("Failed to load validation configuration", e);
        }
    }

    public List<String> getValidEmailDomains() {
        return Collections.unmodifiableList(validEmailDomains);
    }

    public ValidationRule getValidationRule(String id) {
        return validationRules.get(id);
    }

    public Map<String, ValidationRule> getAllValidationRules() {
        return Collections.unmodifiableMap(validationRules);
    }

    public List<ValidationRule> getRulesForField(String fieldName) {
        List<ValidationRule> result = new ArrayList<>();
        for (ValidationRule rule : validationRules.values()) {
            if (rule.getField() != null && rule.getField().equals(fieldName)) {
                result.add(rule);
            }
        }
        return result;
    }
}
