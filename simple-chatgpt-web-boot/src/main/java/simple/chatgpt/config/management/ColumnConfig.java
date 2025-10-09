package simple.chatgpt.config.management;

public class ColumnConfig {
    private String name;
    private String label;
    private boolean visible;
    private boolean sortable;
    private String dbField;
    private String actions;
    private int index = -1;
    private String path; // for nested fields

    public ColumnConfig() {}

    public ColumnConfig(String name, String label, boolean visible, boolean sortable,
                        String dbField, String actions, int index, String path) {
        this.name = name;
        this.label = label;
        this.visible = visible;
        this.sortable = sortable;
        this.dbField = dbField;
        this.actions = actions;
        this.index = index;
        this.path = path;
    }

    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public boolean isSortable() { return sortable; }
    public void setSortable(boolean sortable) { this.sortable = sortable; }

    public String getDbField() { return dbField; }
    public void setDbField(String dbField) { this.dbField = dbField; }

    public String getActions() { return actions; }
    public void setActions(String actions) { this.actions = actions; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}
