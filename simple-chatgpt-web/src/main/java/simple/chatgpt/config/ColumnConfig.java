package simple.chatgpt.config;

public class ColumnConfig {
    private String name;
    private String label;
    private boolean visible;
    private boolean sortable;

    public ColumnConfig() {}
    public ColumnConfig(String name, String label, boolean visible, boolean sortable) {
        this.name = name;
        this.label = label;
        this.visible = visible;
        this.sortable = sortable;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public boolean isSortable() { return sortable; }
    public void setSortable(boolean sortable) { this.sortable = sortable; }
}