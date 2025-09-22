package simple.chatgpt.upload.management.loader;

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

import simple.chatgpt.config.UploadFieldConfig;

public class UploadConfigLoader {

    private static final Logger logger = LogManager.getLogger(UploadConfigLoader.class);
    private static final String CONFIG_FILE = "/config/management/upload-config.xml";

    private static Map<String, List<UploadFieldConfig>> gridConfigs = new HashMap<>();
    private static boolean initialized = false;

    private static void init() {
        if (initialized) return;

        try (InputStream inputStream = UploadConfigLoader.class.getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find " + CONFIG_FILE + " on classpath");
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            NodeList gridNodes = document.getElementsByTagName("grid");
            for (int i = 0; i < gridNodes.getLength(); i++) {
                Element gridElement = (Element) gridNodes.item(i);
                String gridId = gridElement.getAttribute("id");

                List<UploadFieldConfig> columns = new ArrayList<>();
                NodeList colNodes = gridElement.getElementsByTagName("column");
                for (int j = 0; j < colNodes.getLength(); j++) {
                    Element col = (Element) colNodes.item(j);
                    UploadFieldConfig config = new UploadFieldConfig(
                            col.getAttribute("name"),
                            col.getAttribute("dbField"),
                            Integer.parseInt(col.getAttribute("index")),
                            Boolean.parseBoolean(col.getAttribute("required")),
                            col.getAttribute("regex")
                    );
                    columns.add(config);
                }
                gridConfigs.put(gridId, columns);
                logger.debug("Loaded upload grid={} with {} columns", gridId, columns.size());
            }

            initialized = true;

        } catch (Exception e) {
            logger.error("Failed to load upload configuration", e);
            throw new RuntimeException(e);
        }
    }

    public static List<UploadFieldConfig> getColumns(String gridId) {
        init();
        return gridConfigs.getOrDefault(gridId, Collections.emptyList());
    }
}
