package simple.chatgpt.upload.management.loader;

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

public class UploadConfigLoader {

    private static final Logger logger = LogManager.getLogger(UploadConfigLoader.class);
    private static final String CONFIG_FILE = "/config/management/upload-config.xml";

    // Store ColumnConfig lists per grid id
    private static final Map<String, List<ColumnConfig>> gridConfigs = new HashMap<>();
    private static boolean initialized = false;

    private static void init() {
        if (initialized) return;

        try (InputStream inputStream = UploadConfigLoader.class.getResourceAsStream(CONFIG_FILE)) {
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

                List<ColumnConfig> indexed = new ArrayList<>();
                List<ColumnConfig> noIndex = new ArrayList<>();

                NodeList colNodes = gridElement.getElementsByTagName("column");
                for (int j = 0; j < colNodes.getLength(); j++) {
                    Element col = (Element) colNodes.item(j);

                    String name = col.getAttribute("name");
                    String dbField = col.hasAttribute("dbField") ? col.getAttribute("dbField") : null;
                    String label = col.hasAttribute("label") ? col.getAttribute("label") : name;

                    String indexAttr = col.getAttribute("index");
                    int idx = -1; // default when index not specified
                    if (indexAttr != null && !indexAttr.isEmpty()) {
                        try {
                            idx = Integer.parseInt(indexAttr);
                        } catch (NumberFormatException nfe) {
                            logger.warn("Invalid index '{}' for column '{}' in grid '{}', using -1", indexAttr, name, gridId);
                            idx = -1;
                        }
                    }

                    // Create ColumnConfig with full constructor (dbField, actions=null, index)
                    ColumnConfig cfg = new ColumnConfig(name, label, true, false, dbField, null, idx);

                    // Log upload-only attributes
                    String requiredAttr = col.getAttribute("required");
                    String regexAttr = col.getAttribute("regex");
                    logger.debug("  [Upload Column] name={} dbField={} index={} required={} regex={}",
                            name, dbField, idx, requiredAttr, regexAttr);

                    if (idx >= 0) {
                        while (indexed.size() <= idx) indexed.add(null);
                        indexed.set(idx, cfg);
                    } else {
                        noIndex.add(cfg);
                    }
                }

                // Compact indexed list and append no-index columns
                List<ColumnConfig> finalColumns = new ArrayList<>();
                for (ColumnConfig c : indexed) if (c != null) finalColumns.add(c);
                finalColumns.addAll(noIndex);

                gridConfigs.put(gridId, finalColumns);
                logger.debug("Loaded upload grid='{}' with {} columns (indexed: {}, noIndex: {})",
                        gridId, finalColumns.size(), indexed.size(), noIndex.size());
            }

            initialized = true;

        } catch (Exception e) {
            logger.error("Failed to load upload configuration", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the ColumnConfig list for the named upload grid.
     * Columns are ordered according to the 'index' attribute when present.
     */
    public static List<ColumnConfig> getColumns(String gridId) {
        init();
        return gridConfigs.getOrDefault(gridId, Collections.emptyList());
    }
}
