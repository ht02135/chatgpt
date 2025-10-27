package simple.chatgpt.config.management.download;

public class DownloadFieldConfig {
    private String name;
    private String dbField;
    private String label;
    private boolean visible;
    private int index;

    public DownloadFieldConfig(String name, String dbField, String label, boolean visible, int index) {
        this.name = name;
        this.dbField = dbField;
        this.label = label;
        this.visible = visible;
        this.index = index;
    }

    // getters
    public String getName() { return name; }
    public String getDbField() { return dbField; }
    public String getLabel() { return label; }
    public boolean isVisible() { return visible; }
    public int getIndex() { return index; }
}
