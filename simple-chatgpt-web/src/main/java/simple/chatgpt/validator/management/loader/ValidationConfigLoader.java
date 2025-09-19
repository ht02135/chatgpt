package simple.chatgpt.validator.management.loader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ValidationConfigLoader {

    private static final Logger logger = LogManager.getLogger(ValidationConfigLoader.class);
    private static final String CONFIG_FILE = "/config/management/validation-config.xml";

    public static List<String> loadDomains() {
        List<String> domains = new ArrayList<>();

        try (InputStream inputStream = ValidationConfigLoader.class.getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find " + CONFIG_FILE + " on classpath");
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            NodeList nodeList = document.getElementsByTagName("domain");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element domainElement = (Element) nodeList.item(i);
                String domainName = domainElement.getAttribute("domainName").trim();
                if (!domainName.isEmpty()) {
                    logger.debug("Loaded valid email domain: {}", domainName);
                    domains.add(domainName);
                }
            }

            logger.info("Final list of valid email domains: {}", domains);

        } catch (Exception e) {
            logger.error("Failed to load valid email domains", e);
            throw new RuntimeException("Failed to load valid email domains", e);
        }

        return domains;
    }
}
