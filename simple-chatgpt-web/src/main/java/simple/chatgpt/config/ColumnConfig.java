package simple.chatgpt.config;

public class ColumnConfig {
    private String name;
    private String label;
    private boolean visible;
    private boolean sortable;

    // ✅ optional DB column name (for sorting/filtering)
    private String dbField;

    // ✅ optional action group reference
    private String actions;

    public ColumnConfig() {}

    public ColumnConfig(String name, String label, boolean visible, boolean sortable) {
        this.name = name;
        this.label = label;
        this.visible = visible;
        this.sortable = sortable;
    }

    // ✅ single full constructor
    public ColumnConfig(String name, String label, boolean visible, boolean sortable, String dbField, String actions) {
        this.name = name;
        this.label = label;
        this.visible = visible;
        this.sortable = sortable;
        this.dbField = dbField;
        this.actions = actions;
    }

    // ✅ factory method for dbField-based columns
    public static ColumnConfig withDbField(String name, String label, boolean visible, boolean sortable, String dbField) {
        return new ColumnConfig(name, label, visible, sortable, dbField, null);
    }

    // ✅ factory method for action-based columns
    public static ColumnConfig withActions(String name, String label, boolean visible, boolean sortable, String actions) {
        return new ColumnConfig(name, label, visible, sortable, null, actions);
    }

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
}
