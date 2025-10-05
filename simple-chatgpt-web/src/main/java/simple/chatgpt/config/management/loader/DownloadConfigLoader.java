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
public class DownloadConfigLoader {

    private static final Logger logger = LogManager.getLogger(DownloadConfigLoader.class);

    private final PropertyManagementService propertyService;
    private final Map<String, List<ColumnConfig>> gridConfigs = new HashMap<>();

    // Default fallback config
    private static final String DEFAULT_CONFIG_FILE = "/config/management/download-config.xml";

    public DownloadConfigLoader(PropertyManagementService propertyService) {
        this.propertyService = propertyService;
    }

    @PostConstruct
    private void init() {
    	
    	logger.debug("init called #############");
        String configFilePath = DEFAULT_CONFIG_FILE;
        try {
            configFilePath = propertyService.getString(PropertyKey.DOWNLOAD_CONFIG_RELATIVE_PATH);
            logger.debug("DownloadConfigLoader: Loaded property DOWNLOAD_CONFIG_RELATIVE_PATH={}", configFilePath);
        } catch (Exception e) {
            logger.error("DownloadConfigLoader: Failed to fetch DOWNLOAD_CONFIG_RELATIVE_PATH, using default {}", configFilePath, e);
        }
        logger.debug("init: configFilePath={}", configFilePath);
        logger.debug("init called #############");

        try (InputStream inputStream = DownloadConfigLoader.class.getResourceAsStream(configFilePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find download config: " + configFilePath);
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
                    String dbField = col.getAttribute("dbField");
                    String label = col.hasAttribute("label") ? col.getAttribute("label") : name;
                    boolean visible = Boolean.parseBoolean(col.getAttribute("visible"));
                    String indexAttr = col.getAttribute("index");

                    int idx = -1;
                    if (indexAttr != null && !indexAttr.isEmpty()) {
                        try { idx = Integer.parseInt(indexAttr); }
                        catch (NumberFormatException nfe) {
                            logger.warn("Invalid index '{}' for column '{}' in grid '{}', treating as no-index",
                                    indexAttr, name, gridId);
                            idx = -1;
                        }
                    }

                    ColumnConfig cfg = new ColumnConfig(name, label, visible, true, dbField, null, idx);

                    if (idx >= 0) {
                        while (indexed.size() <= idx) indexed.add(null);
                        indexed.set(idx, cfg);
                    } else {
                        noIndex.add(cfg);
                    }

                    logger.debug("Loaded column for grid '{}': name={}, label={}, index={}", gridId, name, label, idx);
                }

                List<ColumnConfig> finalColumns = new ArrayList<>();
                for (ColumnConfig c : indexed) if (c != null) finalColumns.add(c);
                finalColumns.addAll(noIndex);

                gridConfigs.put(gridId, finalColumns);
                logger.debug("Grid '{}' loaded with {} columns", gridId, finalColumns.size());
            }

        } catch (Exception e) {
            logger.error("Failed to load download configuration from {}", configFilePath, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the column configuration for a given grid.
     */
    public List<ColumnConfig> getColumns(String gridId) {
        return gridConfigs.getOrDefault(gridId, Collections.emptyList());
    }
}
