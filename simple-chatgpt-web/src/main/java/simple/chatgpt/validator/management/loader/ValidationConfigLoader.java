package simple.chatgpt.validator.management.loader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import simple.chatgpt.validator.management.rule.ValidationRule;

public class ValidationConfigLoader {

    private static final Logger logger = LogManager.getLogger(ValidationConfigLoader.class);
    private static final String CONFIG_FILE = "/config/management/validation-config.xml";

    private static List<String> validEmailDomains = new ArrayList<>();
    private static Map<String, ValidationRule> validationRules = new HashMap<>();
    private static boolean initialized = false;

    private static void init() {
        if (initialized) return;

        try (InputStream inputStream = ValidationConfigLoader.class.getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find " + CONFIG_FILE + " on classpath");
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            // --- Load <validation> rules ---
            NodeList validationNodes = document.getElementsByTagName("validation");
            for (int i = 0; i < validationNodes.getLength(); i++) {
                Element validationElement = (Element) validationNodes.item(i);
                String id = validationElement.getAttribute("id");
                String field = validationElement.getAttribute("field"); // NEW: map field
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
            logger.error("Failed to load validation configuration", e);
            throw new RuntimeException("Failed to load validation configuration", e);
        }
    }

    public static List<String> getValidEmailDomains() {
        init();
        return Collections.unmodifiableList(validEmailDomains);
    }

    public static ValidationRule getValidationRule(String id) {
        init();
        return validationRules.get(id);
    }

    public static Map<String, ValidationRule> getAllValidationRules() {
        init();
        return Collections.unmodifiableMap(validationRules);
    }

    // --- Utility: get rules by field name for dynamic validation ---
    public static List<ValidationRule> getRulesForField(String fieldName) {
        init();
        List<ValidationRule> result = new ArrayList<>();
        for (ValidationRule rule : validationRules.values()) {
            if (rule.getField() != null && rule.getField().equals(fieldName)) {
                result.add(rule);
            }
        }
        return result;
    }
}
