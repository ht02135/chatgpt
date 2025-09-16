package simple.chatgpt.config;

public class ColumnConfig {
    private String name;
    private String label;
    private boolean visible;
    private boolean sortable;

    // ✅ new: optional action group reference
    private String actions;

    public ColumnConfig() {}

    public ColumnConfig(String name, String label, boolean visible, boolean sortable) {
        this.name = name;
        this.label = label;
        this.visible = visible;
        this.sortable = sortable;
    }

    // ✅ new constructor variant for action columns
    public ColumnConfig(String name, String label, boolean visible, boolean sortable, String actions) {
        this.name = name;
        this.label = label;
        this.visible = visible;
        this.sortable = sortable;
        this.actions = actions;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public boolean isSortable() { return sortable; }
    public void setSortable(boolean sortable) { this.sortable = sortable; }

    // ✅ getter/setter for actions
    public String getActions() { return actions; }
    public void setActions(String actions) { this.actions = actions; }
}
