package simple.chatgpt.config.management;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import simple.chatgpt.config.ActionConfig;
import simple.chatgpt.config.ActionGroupConfig;
import simple.chatgpt.config.ColumnConfig;
import simple.chatgpt.config.FieldConfig;
import simple.chatgpt.config.FormConfig;
import simple.chatgpt.config.GridConfig;
import simple.chatgpt.config.RegexConfig;
import simple.chatgpt.config.ValidatorConfig;
import simple.chatgpt.config.ValidatorGroupConfig;

public class ManagementConfigLoader {

    private static final Logger logger = LogManager.getLogger(ManagementConfigLoader.class);
    private static final String CONFIG_FILE = "/config/management/config.xml";

    /** Load XML document from classpath */
    private Document loadDocument() throws Exception {
        try (InputStream is = ManagementConfigLoader.class.getResourceAsStream(CONFIG_FILE)) {
            if (is == null) throw new RuntimeException("Config file not found: " + CONFIG_FILE);
            logger.info("Loaded config file: {}", CONFIG_FILE);
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        }
    }

    /** Load all grid definitions */
    public List<GridConfig> loadGrids() throws Exception {
        Document doc = loadDocument();
        NodeList gridNodes = doc.getElementsByTagName("grid");
        List<GridConfig> grids = new ArrayList<>();

        for (int i = 0; i < gridNodes.getLength(); i++) {
            Element gridEl = (Element) gridNodes.item(i);
            GridConfig grid = new GridConfig(gridEl.getAttribute("id"));
            logger.info("Loading Grid: {}", grid.getId());

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

                ColumnConfig column;
                if (c.hasAttribute("actions")) {
                    column = new ColumnConfig(name, label, visible, sortable, null, c.getAttribute("actions"), index);
                    logger.debug("  [Column] name={} actions={} index={}", name, c.getAttribute("actions"), index);
                } else {
                    String dbField = c.hasAttribute("dbField") ? c.getAttribute("dbField") : null;
                    column = new ColumnConfig(name, label, visible, sortable, dbField, null, index);
                    logger.debug("  [Column] name={} dbField={} index={}", name, dbField, index);
                }

                grid.addColumn(column);
            }
            grids.add(grid);
        }
        return grids;
    }

    /** Load all form definitions */
    public List<FormConfig> loadForms() throws Exception {
        Document doc = loadDocument();
        NodeList formNodes = doc.getElementsByTagName("form");
        List<FormConfig> forms = new ArrayList<>();

        for (int i = 0; i < formNodes.getLength(); i++) {
            Element formEl = (Element) formNodes.item(i);
            FormConfig form = new FormConfig(formEl.getAttribute("id"));
            logger.info("Loading Form: {}", form.getId());

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
                logger.debug("  [Field] name={} label={} regex={} validators={}",
                        field.getName(), field.getLabel(), field.getRegex(), validatorIds);

                form.addField(field);
            }
            forms.add(form);
        }
        return forms;
    }

    /** Load regex validators */
    public List<RegexConfig> loadRegexes() throws Exception {
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
            logger.info("[Regex] id={} expr={}", regex.getId(), regex.getExpression());
        }
        return regexes;
    }

    /** Load action groups */
    public List<ActionGroupConfig> loadActionGroups() throws Exception {
        Document doc = loadDocument();
        NodeList actionGroupNodes = doc.getElementsByTagName("actions");
        List<ActionGroupConfig> groups = new ArrayList<>();

        for (int i = 0; i < actionGroupNodes.getLength(); i++) {
            Element groupEl = (Element) actionGroupNodes.item(i);
            ActionGroupConfig group = new ActionGroupConfig(groupEl.getAttribute("id"));
            logger.info("Loading Actions group: {}", group.getId());

            NodeList actions = groupEl.getElementsByTagName("action");
            for (int j = 0; j < actions.getLength(); j++) {
                Element a = (Element) actions.item(j);

                String name = a.getAttribute("name");
                String label = a.getAttribute("label");
                String jsMethod = a.getAttribute("jsMethod");
                boolean visible = !a.hasAttribute("visible") || Boolean.parseBoolean(a.getAttribute("visible"));

                ActionConfig action = new ActionConfig(name, label, visible, jsMethod);
                group.addAction(action);
                logger.debug("  [Action] name={} label={} jsMethod={}", name, label, jsMethod);
            }
            groups.add(group);
        }
        return groups;
    }

    /** Load validator groups */
    public List<ValidatorGroupConfig> loadValidators() throws Exception {
        Document doc = loadDocument();
        NodeList validatorGroupNodes = doc.getElementsByTagName("validators");
        List<ValidatorGroupConfig> groups = new ArrayList<>();

        for (int i = 0; i < validatorGroupNodes.getLength(); i++) {
            Element groupEl = (Element) validatorGroupNodes.item(i);
            ValidatorGroupConfig group = new ValidatorGroupConfig(groupEl.getAttribute("id"));
            logger.info("Loading Validator Group: {}", group.getId());

            NodeList validators = groupEl.getElementsByTagName("validator");
            for (int j = 0; j < validators.getLength(); j++) {
                Element v = (Element) validators.item(j);
                ValidatorConfig validator = new ValidatorConfig(
                        v.getAttribute("type"),
                        v.getAttribute("validRegexExpression"),
                        v.getAttribute("errorMessage")
                );
                group.addValidator(validator);
                logger.debug("  [Validator] type={} expr={}", validator.getType(), validator.getValidRegexExpression());
            }
            groups.add(group);
        }
        return groups;
    }
}
