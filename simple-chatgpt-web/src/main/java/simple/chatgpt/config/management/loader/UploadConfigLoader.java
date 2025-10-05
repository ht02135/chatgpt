package simple.chatgpt.config.management.loader;

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

import simple.chatgpt.config.ColumnConfig;
import simple.chatgpt.service.management.PropertyManagementService;
import simple.chatgpt.util.PropertyKey;

@Component
public class UploadConfigLoader {

    private static final Logger logger = LogManager.getLogger(UploadConfigLoader.class);

    private final PropertyManagementService propertyService;
    private final Map<String, List<ColumnConfig>> gridConfigs = new HashMap<>();

    private static final String DEFAULT_CONFIG_FILE = "/config/management/upload-config.xml";

    public UploadConfigLoader(PropertyManagementService propertyService) {
        this.propertyService = propertyService;
    }

    @PostConstruct
    private void init() {
    	
        logger.debug("init called #############");
        String configFilePath = DEFAULT_CONFIG_FILE;
        try {
            configFilePath = propertyService.getString(PropertyKey.UPLOAD_CONFIG_RELATIVE_PATH);
            logger.debug("UploadConfigLoader: Loaded property UPLOAD_CONFIG_RELATIVE_PATH={}", configFilePath);
        } catch (Exception e) {
            logger.error("UploadConfigLoader: Failed to fetch UPLOAD_CONFIG_RELATIVE_PATH, using default {}", configFilePath, e);
        }
        logger.debug("init: configFilePath={}", configFilePath);
        logger.debug("init called #############");

        try (InputStream inputStream = UploadConfigLoader.class.getResourceAsStream(configFilePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find upload config: " + configFilePath);
            }

            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(inputStream);

            NodeList gridNodes = document.getElementsByTagName("grid");
            for (int i = 0; i < gridNodes.getLength(); i++) {
                Element gridElement = (Element) gridNodes.item(i);
                String gridId = gridElement.getAttribute("id");

                List<ColumnConfig> indexed = new ArrayList<>();
                List<ColumnConfig> noIndex = new ArrayList<>();

                NodeList colNodes = gridElement.getElementsByTagName("column");
                for (int j = 0; j < colNodes.getLength(); j++) {
                    Element col = (Element) colNodes.item(j);

                    String name = col.getAttribute("name");
                    String dbField = col.hasAttribute("dbField") ? col.getAttribute("dbField") : null;
                    String label = col.hasAttribute("label") ? col.getAttribute("label") : name;

                    String indexAttr = col.getAttribute("index");
                    int idx = -1;
                    if (indexAttr != null && !indexAttr.isEmpty()) {
                        try { idx = Integer.parseInt(indexAttr); }
                        catch (NumberFormatException nfe) {
                            logger.warn("Invalid index '{}' for column '{}' in grid '{}', using -1", indexAttr, name, gridId);
                        }
                    }

                    ColumnConfig cfg = new ColumnConfig(name, label, true, false, dbField, null, idx);

                    // Log upload-specific attributes
                    String requiredAttr = col.getAttribute("required");
                    String regexAttr = col.getAttribute("regex");
                    logger.debug("Loaded column for upload grid '{}': name={}, dbField={}, index={}, required={}, regex={}",
                            gridId, name, dbField, idx, requiredAttr, regexAttr);

                    if (idx >= 0) {
                        while (indexed.size() <= idx) indexed.add(null);
                        indexed.set(idx, cfg);
                    } else {
                        noIndex.add(cfg);
                    }
                }

                List<ColumnConfig> finalColumns = new ArrayList<>();
                for (ColumnConfig c : indexed) if (c != null) finalColumns.add(c);
                finalColumns.addAll(noIndex);

                gridConfigs.put(gridId, finalColumns);
                logger.debug("Upload grid '{}' loaded with {} columns (indexed: {}, noIndex: {})",
                        gridId, finalColumns.size(), indexed.size(), noIndex.size());
            }

        } catch (Exception e) {
            logger.error("Failed to load upload configuration from {}", configFilePath, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the column configuration for a given upload grid.
     */
    public List<ColumnConfig> getColumns(String gridId) {
        return gridConfigs.getOrDefault(gridId, Collections.emptyList());
    }
}
