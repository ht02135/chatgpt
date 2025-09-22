package simple.chatgpt.config;

public class ColumnConfig {
    private String name;
    private String label;
    private boolean visible;
    private boolean sortable;
    private String dbField;
    private String actions;
    private int index = -1; // default -1 means no index

    public ColumnConfig() {}

    // basic constructor
    public ColumnConfig(String name, String label, boolean visible, boolean sortable) {
        this.name = name;
        this.label = label;
        this.visible = visible;
        this.sortable = sortable;
    }

    // full constructor including dbField, actions, index
    public ColumnConfig(String name, String label, boolean visible, boolean sortable, String dbField, String actions, int index) {
        this.name = name;
        this.label = label;
        this.visible = visible;
        this.sortable = sortable;
        this.dbField = dbField;
        this.actions = actions;
        this.index = index;
    }

    // factory method for dbField
    public static ColumnConfig withDbField(String name, String label, boolean visible, boolean sortable, String dbField, int index) {
        return new ColumnConfig(name, label, visible, sortable, dbField, null, index);
    }

    // factory method for actions
    public static ColumnConfig withActions(String name, String label, boolean visible, boolean sortable, String actions, int index) {
        return new ColumnConfig(name, label, visible, sortable, null, actions, index);
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
}
