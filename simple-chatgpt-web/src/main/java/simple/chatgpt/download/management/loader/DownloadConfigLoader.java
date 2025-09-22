package simple.chatgpt.download.management.loader;

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

import simple.chatgpt.config.DownloadFieldConfig;

public class DownloadConfigLoader {

    private static final Logger logger = LogManager.getLogger(DownloadConfigLoader.class);
    private static final String CONFIG_FILE = "/config/management/download-config.xml";

    private static Map<String, List<DownloadFieldConfig>> gridConfigs = new HashMap<>();
    private static boolean initialized = false;

    private static void init() {
        if (initialized) return;

        try (InputStream inputStream = DownloadConfigLoader.class.getResourceAsStream(CONFIG_FILE)) {
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

                List<DownloadFieldConfig> columns = new ArrayList<>();
                NodeList colNodes = gridElement.getElementsByTagName("column");
                for (int j = 0; j < colNodes.getLength(); j++) {
                    Element col = (Element) colNodes.item(j);

                    DownloadFieldConfig config = new DownloadFieldConfig(
                            col.getAttribute("name"),
                            col.getAttribute("dbField"),
                            col.getAttribute("label"),
                            Boolean.parseBoolean(col.getAttribute("visible")),
                            Integer.parseInt(col.getAttribute("index"))
                    );
                    columns.add(config);
                }
                gridConfigs.put(gridId, columns);
                logger.debug("Loaded download grid={} with {} columns", gridId, columns.size());
            }

            initialized = true;

        } catch (Exception e) {
            logger.error("Failed to load download configuration", e);
            throw new RuntimeException(e);
        }
    }

    public static List<DownloadFieldConfig> getColumns(String gridId) {
        init();
        return gridConfigs.getOrDefault(gridId, Collections.emptyList());
    }
}
