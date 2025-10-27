package simple.chatgpt.config.management.loader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import simple.chatgpt.config.management.ActionConfig;
import simple.chatgpt.config.management.ActionGroupConfig;
import simple.chatgpt.config.management.ColumnConfig;
import simple.chatgpt.config.management.FieldConfig;
import simple.chatgpt.config.management.FormConfig;
import simple.chatgpt.config.management.GridConfig;
import simple.chatgpt.config.management.RegexConfig;
import simple.chatgpt.config.management.ValidatorGroupConfig;
import simple.chatgpt.config.management.validation.ValidatorConfig;
import simple.chatgpt.service.management.PropertyManagementService;
import simple.chatgpt.util.PropertyKey;

@Component
public class ManagementConfigLoader {

    private static final Logger logger = LogManager.getLogger(ManagementConfigLoader.class);

    private static final String DEFAULT_CONFIG_FILE = "/config/management/config.xml";

    @Autowired
    private PropertyManagementService propertyService;

    private String configFilePath;

    @PostConstruct
    private void initConfigPath() {
        configFilePath = DEFAULT_CONFIG_FILE;
        try {
            configFilePath = propertyService.getString(PropertyKey.MANAGEMENT_CONFIG_RELATIVE_PATH);
            logger.debug("initConfigPath: Loaded property configFilePath={}", configFilePath);
        } catch (Exception e) {
            logger.error("initConfigPath: Failed to load property configFilePath={}", DEFAULT_CONFIG_FILE, e);
        }
        logger.debug("initConfigPath: configFilePath={}", configFilePath);
    }

    /** Load XML document from classpath */
    private Document loadDocument() throws Exception {
        logger.info("loadDocument: called");
        try (InputStream is = ManagementConfigLoader.class.getResourceAsStream(configFilePath)) {
            if (is == null) throw new RuntimeException("Config file not found: " + configFilePath);
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        }
    }

    /** Load all grid definitions */
    public List<GridConfig> loadGrids() throws Exception {
        logger.info("loadGrids: called");

        Document doc = loadDocument();
        NodeList gridNodes = doc.getElementsByTagName("grid");
        List<GridConfig> grids = new ArrayList<>();

        for (int i = 0; i < gridNodes.getLength(); i++) {
            Element gridEl = (Element) gridNodes.item(i);
            GridConfig grid = new GridConfig(gridEl.getAttribute("id"));

            NodeList cols = gridEl.getElementsByTagName("column");
            for (int j = 0; j < cols.getLength(); j++) {
                Element c = (Element) cols.item(j);

                String name = c.getAttribute("name");
                String label = c.hasAttribute("label") ? c.getAttribute("label") : name;
                boolean visible = c.hasAttribute("visible") && Boolean.parseBoolean(c.getAttribute("visible"));
                boolean sortable = c.hasAttribute("sortable") && Boolean.parseBoolean(c.getAttribute("sortable"));
                int index = -1;
                if (c.hasAttribute("index")) {
                    try {
                        index = Integer.parseInt(c.getAttribute("index"));
                    } catch (NumberFormatException nfe) {
                        logger.warn("Invalid index '{}' for column '{}' in grid '{}', defaulting to -1",
                                c.getAttribute("index"), name, grid.getId());
                    }
                }

                // New: optional path for nested fields
                String path = c.hasAttribute("path") ? c.getAttribute("path") : null;
                String type = c.hasAttribute("type") ? c.getAttribute("type") : null;

                ColumnConfig column;
                if (c.hasAttribute("actions")) {
                    column = new ColumnConfig(name, label, visible, sortable, null, c.getAttribute("actions"), index, path, type);
                } else {
                    String dbField = c.hasAttribute("dbField") ? c.getAttribute("dbField") : null;
                    column = new ColumnConfig(name, label, visible, sortable, dbField, null, index, path, type);
                }

                grid.addColumn(column);

                logger.debug("Loaded column for grid '{}': name={}, label={}, dbField={}, actions={}, path={}, index={}",
                        grid.getId(), name, label, column.getDbField(), column.getActions(), path, index);
            }

            grids.add(grid);
        }
        return grids;
    }

    /** Load all form definitions */
    public List<FormConfig> loadForms() throws Exception {
        logger.info("loadForms: called");

        Document doc = loadDocument();
        NodeList formNodes = doc.getElementsByTagName("form");
        List<FormConfig> forms = new ArrayList<>();

        for (int i = 0; i < formNodes.getLength(); i++) {
            Element formEl = (Element) formNodes.item(i);
            FormConfig form = new FormConfig(formEl.getAttribute("id"));

            NodeList fields = formEl.getElementsByTagName("field");
            for (int j = 0; j < fields.getLength(); j++) {
                Element f = (Element) fields.item(j);

                String validatorAttr = f.hasAttribute("validators") ? f.getAttribute("validators") : null;
                List<String> validatorIds = new ArrayList<>();
                if (validatorAttr != null && !validatorAttr.isEmpty()) {
                    validatorIds.addAll(Arrays.asList(validatorAttr.split(",")));
                }

                FieldConfig field = new FieldConfig(
                        f.getAttribute("name"),
                        f.getAttribute("label"),
                        f.hasAttribute("visible") && Boolean.parseBoolean(f.getAttribute("visible")),
                        f.hasAttribute("required") && Boolean.parseBoolean(f.getAttribute("required")),
                        f.hasAttribute("editable") && Boolean.parseBoolean(f.getAttribute("editable")),
                        f.hasAttribute("regex") ? f.getAttribute("regex") : null,
                        validatorIds
                );

                form.addField(field);
            }
            forms.add(form);
        }
        return forms;
    }

    /** Load regex validators */
    public List<RegexConfig> loadRegexes() throws Exception {
        logger.info("loadRegexes: called");

        Document doc = loadDocument();
        NodeList regexNodes = doc.getElementsByTagName("regex");
        List<RegexConfig> regexes = new ArrayList<>();

        for (int i = 0; i < regexNodes.getLength(); i++) {
            Element r = (Element) regexNodes.item(i);
            RegexConfig regex = new RegexConfig(
                    r.getAttribute("id"),
                    r.getAttribute("validRegexExpression"),
                    r.getAttribute("errorMessage")
            );
            regexes.add(regex);
        }
        return regexes;
    }

    /** Load action groups */
    public List<ActionGroupConfig> loadActionGroups() throws Exception {
        logger.info("loadActionGroups: called");

        Document doc = loadDocument();
        NodeList actionGroupNodes = doc.getElementsByTagName("actions");
        List<ActionGroupConfig> groups = new ArrayList<>();

        for (int i = 0; i < actionGroupNodes.getLength(); i++) {
            Element groupEl = (Element) actionGroupNodes.item(i);
            ActionGroupConfig group = new ActionGroupConfig(groupEl.getAttribute("id"));

            NodeList actions = groupEl.getElementsByTagName("action");
            for (int j = 0; j < actions.getLength(); j++) {
                Element a = (Element) actions.item(j);

                String name = a.getAttribute("name");
                String label = a.getAttribute("label");
                String jsMethod = a.getAttribute("jsMethod");
                boolean visible = !a.hasAttribute("visible") || Boolean.parseBoolean(a.getAttribute("visible"));

                ActionConfig action = new ActionConfig(name, label, visible, jsMethod);
                group.addAction(action);
            }
            groups.add(group);
        }
        return groups;
    }

    /** Load validator groups */
    public List<ValidatorGroupConfig> loadValidators() throws Exception {
        logger.info("loadValidators: called");

        Document doc = loadDocument();
        NodeList validatorGroupNodes = doc.getElementsByTagName("validators");
        List<ValidatorGroupConfig> groups = new ArrayList<>();

        for (int i = 0; i < validatorGroupNodes.getLength(); i++) {
            Element groupEl = (Element) validatorGroupNodes.item(i);
            ValidatorGroupConfig group = new ValidatorGroupConfig(groupEl.getAttribute("id"));

            NodeList validators = groupEl.getElementsByTagName("validator");
            for (int j = 0; j < validators.getLength(); j++) {
                Element v = (Element) validators.item(j);
                ValidatorConfig validator = new ValidatorConfig(
                        v.getAttribute("type"),
                        v.getAttribute("validRegexExpression"),
                        v.getAttribute("errorMessage")
                );
                group.addValidator(validator);
            }
            groups.add(group);
        }
        return groups;
    }
}
