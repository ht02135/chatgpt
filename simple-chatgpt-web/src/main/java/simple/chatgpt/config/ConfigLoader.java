package simple.chatgpt.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
                grid.addColumn(new ColumnConfig(
                        c.getAttribute("name"),
                        c.getAttribute("label"),
                        Boolean.parseBoolean(c.getAttribute("visible")),
                        Boolean.parseBoolean(c.getAttribute("sortable"))
                ));
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
                        Boolean.parseBoolean(f.getAttribute("editable"))
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
                    r.getAttribute("vaildRegexExpression"),
                    r.getAttribute("errorMessage")
            ));
        }
        return regexes;
    }
}
