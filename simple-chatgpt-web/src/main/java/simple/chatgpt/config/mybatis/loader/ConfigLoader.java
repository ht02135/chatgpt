package simple.chatgpt.config.mybatis.loader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

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

public class ConfigLoader {

    private static final String CONFIG_FILE = "/config/config.xml";

    private Document loadDocument() throws Exception {
        try (InputStream is = ConfigLoader.class.getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                throw new RuntimeException("Config file not found: " + CONFIG_FILE);
            }
            return DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(is);
        }
    }

    public List<GridConfig> loadGrids() throws Exception {
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
                String label = c.getAttribute("label");
                boolean visible = Boolean.parseBoolean(c.getAttribute("visible"));
                boolean sortable = Boolean.parseBoolean(c.getAttribute("sortable"));
                int index = c.hasAttribute("index") ? Integer.parseInt(c.getAttribute("index")) : j;

                ColumnConfig column;
                if (c.hasAttribute("actions")) {
                    // action column
                    column = ColumnConfig.withActions(
                            name,
                            label,
                            visible,
                            sortable,
                            c.getAttribute("actions"),
                            index
                    );
                } else {
                    // normal sortable/data column with optional dbField
                    String dbField = c.hasAttribute("dbField") ? c.getAttribute("dbField") : null;
                    column = ColumnConfig.withDbField(
                            name,
                            label,
                            visible,
                            sortable,
                            dbField,
                            index
                    );
                }

                grid.addColumn(column);
            }
            grids.add(grid);
        }
        return grids;
    }

    public List<FormConfig> loadForms() throws Exception {
        Document doc = loadDocument();
        NodeList formNodes = doc.getElementsByTagName("form");
        List<FormConfig> forms = new ArrayList<>();

        for (int i = 0; i < formNodes.getLength(); i++) {
            Element formEl = (Element) formNodes.item(i);
            FormConfig form = new FormConfig(formEl.getAttribute("id"));

            NodeList fields = formEl.getElementsByTagName("field");
            for (int j = 0; j < fields.getLength(); j++) {
                Element f = (Element) fields.item(j);

                form.addField(new FieldConfig(
                        f.getAttribute("name"),
                        f.getAttribute("label"),
                        Boolean.parseBoolean(f.getAttribute("visible")),
                        Boolean.parseBoolean(f.getAttribute("required")),
                        Boolean.parseBoolean(f.getAttribute("editable")),
                        f.getAttribute("regex"),
                        f.hasAttribute("validators") ? f.getAttribute("validators") : null
                ));
            }
            forms.add(form);
        }
        return forms;
    }

    public List<RegexConfig> loadRegexes() throws Exception {
        Document doc = loadDocument();
        NodeList regexNodes = doc.getElementsByTagName("regex");
        List<RegexConfig> regexes = new ArrayList<>();

        for (int i = 0; i < regexNodes.getLength(); i++) {
            Element r = (Element) regexNodes.item(i);
            regexes.add(new RegexConfig(
                    r.getAttribute("id"),
                    r.getAttribute("validRegexExpression"),
                    r.getAttribute("errorMessage")
            ));
        }
        return regexes;
    }

    public List<ActionGroupConfig> loadActionGroups() throws Exception {
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

                boolean visible = true;
                if (a.hasAttribute("visible")) {
                    visible = Boolean.parseBoolean(a.getAttribute("visible"));
                }

                group.addAction(new ActionConfig(name, label, visible, jsMethod ));
            }
            groups.add(group);
        }
        return groups;
    }

    public List<ValidatorGroupConfig> loadValidators() throws Exception {
        Document doc = loadDocument();
        NodeList validatorGroupNodes = doc.getElementsByTagName("validators");
        List<ValidatorGroupConfig> groups = new ArrayList<>();

        for (int i = 0; i < validatorGroupNodes.getLength(); i++) {
            Element groupEl = (Element) validatorGroupNodes.item(i);
            ValidatorGroupConfig group = new ValidatorGroupConfig(groupEl.getAttribute("id"));

            NodeList validators = groupEl.getElementsByTagName("validator");
            for (int j = 0; j < validators.getLength(); j++) {
                Element v = (Element) validators.item(j);
                group.addValidator(new ValidatorConfig(
                        v.getAttribute("type"),
                        v.getAttribute("validRegexExpression"),
                        v.getAttribute("errorMessage")
                ));
            }
            groups.add(group);
        }
        return groups;
    }

}
