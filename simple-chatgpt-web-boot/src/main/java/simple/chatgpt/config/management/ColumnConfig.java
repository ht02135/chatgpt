package simple.chatgpt.config.management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ColumnConfig {
    private static final Logger logger = LogManager.getLogger(ColumnConfig.class);

    private String name;
    private String label;
    private boolean visible;
    private boolean sortable;
    private String dbField;
    private String actions;
    private int index = -1;
    private String path; // for nested fields
    private String type; // <== NEW: e.g. "url", "date", "money"

    public ColumnConfig() {
        logger.debug("ColumnConfig constructor called");
    }

    public ColumnConfig(String name, String label, boolean visible, boolean sortable,
                        String dbField, String actions, int index, String path, String type) {
        logger.debug("ColumnConfig constructor called with params:");
        logger.debug("name={}", name);
        logger.debug("label={}", label);
        logger.debug("visible={}", visible);
        logger.debug("sortable={}", sortable);
        logger.debug("dbField={}", dbField);
        logger.debug("actions={}", actions);
        logger.debug("index={}", index);
        logger.debug("path={}", path);
        logger.debug("type={}", type);

        this.name = name;
        this.label = label;
        this.visible = visible;
        this.sortable = sortable;
        this.dbField = dbField;
        this.actions = actions;
        this.index = index;
        this.path = path;
        this.type = type;
    }

    // ===== Getters/Setters with logging =====

    public String getName() { return name; }
    public void setName(String name) {
        logger.debug("setName called with name={}", name);
        this.name = name;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) {
        logger.debug("setLabel called with label={}", label);
        this.label = label;
    }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) {
        logger.debug("setVisible called with visible={}", visible);
        this.visible = visible;
    }

    public boolean isSortable() { return sortable; }
    public void setSortable(boolean sortable) {
        logger.debug("setSortable called with sortable={}", sortable);
        this.sortable = sortable;
    }

    public String getDbField() { return dbField; }
    public void setDbField(String dbField) {
        logger.debug("setDbField called with dbField={}", dbField);
        this.dbField = dbField;
    }

    public String getActions() { return actions; }
    public void setActions(String actions) {
        logger.debug("setActions called with actions={}", actions);
        this.actions = actions;
    }

    public int getIndex() { return index; }
    public void setIndex(int index) {
        logger.debug("setIndex called with index={}", index);
        this.index = index;
    }

    public String getPath() { return path; }
    public void setPath(String path) {
        logger.debug("setPath called with path={}", path);
        this.path = path;
    }

    public String getType() { return type; }
    public void setType(String type) {
        logger.debug("setType called with type={}", type);
        this.type = type;
    }

    @Override
    public String toString() {
        return "ColumnConfig{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", visible=" + visible +
                ", sortable=" + sortable +
                ", dbField='" + dbField + '\'' +
                ", actions='" + actions + '\'' +
                ", index=" + index +
                ", path='" + path + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
