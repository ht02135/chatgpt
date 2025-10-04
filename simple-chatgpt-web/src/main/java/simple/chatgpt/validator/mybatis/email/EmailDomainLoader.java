package simple.chatgpt.validator.mybatis.email;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class EmailDomainLoader {

    private static final String CONFIG_FILE = "/validator/valid-email-domains.xml";

    public static List<String> loadDomains() {
        List<String> domains = new ArrayList<>();

        try (InputStream inputStream = EmailDomainLoader.class.getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find " + CONFIG_FILE + " on classpath");
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            NodeList nodeList = document.getElementsByTagName("domain");
            for (int i = 0; i < nodeList.getLength(); i++) {
                domains.add(nodeList.item(i).getTextContent().trim());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load valid email domains", e);
        }

        return domains;
    }
}
