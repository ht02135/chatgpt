package simple.chatgpt.download.management.loader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import simple.chatgpt.config.ColumnConfig;

public class DownloadConfigLoader {

    private static final Logger logger = LogManager.getLogger(DownloadConfigLoader.class);
    private static final String CONFIG_FILE = "/config/management/download-config.xml";

    // Store ColumnConfig lists per grid id
    private static final Map<String, List<ColumnConfig>> gridConfigs = new HashMap<>();
    private static boolean initialized = false;

    private static void init() {
        if (initialized) return;

        try (InputStream inputStream = DownloadConfigLoader.class.getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find " + CONFIG_FILE + " on classpath");
            }

            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(inputStream);

            NodeList gridNodes = document.getElementsByTagName("grid");
            for (int i = 0; i < gridNodes.getLength(); i++) {
                Element gridElement = (Element) gridNodes.item(i);
                String gridId = gridElement.getAttribute("id");

                // Temporary lists for ordered columns
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

                    // Parse index
                    int idx = -1;
                    if (indexAttr != null && !indexAttr.isEmpty()) {
                        try {
                            idx = Integer.parseInt(indexAttr);
                        } catch (NumberFormatException nfe) {
                            logger.warn("Invalid index '{}' for column '{}' in grid '{}', treating as no-index",
                                    indexAttr, name, gridId);
                            idx = -1;
                        }
                    }

                    // Create ColumnConfig (all download columns sortable=true)
                    ColumnConfig cfg = new ColumnConfig(name, label, visible, true, dbField, null, idx);

                    logger.debug("  [Download Column] name={} dbField={} label={} visible={} index={}",
                            name, dbField, label, visible, idx);

                    // Add to indexed or noIndex list
                    if (idx >= 0) {
                        while (indexed.size() <= idx) indexed.add(null);
                        indexed.set(idx, cfg);
                    } else {
                        noIndex.add(cfg);
                    }
                }

                // Build final column list (indexed first, then noIndex)
                List<ColumnConfig> finalColumns = new ArrayList<>();
                for (ColumnConfig c : indexed) if (c != null) finalColumns.add(c);
                finalColumns.addAll(noIndex);

                gridConfigs.put(gridId, finalColumns);
                logger.debug("Loaded download grid='{}' with {} columns (indexed: {}, noIndex: {})",
                        gridId, finalColumns.size(), indexed.size(), noIndex.size());
            }

            initialized = true;

        } catch (Exception e) {
            logger.error("Failed to load download configuration", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the ColumnConfig list for the given download grid.
     * Columns are ordered according to 'index' when provided.
     */
    public static List<ColumnConfig> getColumns(String gridId) {
        init();
        return gridConfigs.getOrDefault(gridId, Collections.emptyList());
    }
}
