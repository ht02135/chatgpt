package simple.chatgpt.config;

public class UploadFieldConfig {
    private String name;
    private String dbField;
    private int index;
    private boolean required;
    private String regex;

    public UploadFieldConfig(String name, String dbField, int index, boolean required, String regex) {
        this.name = name;
        this.dbField = dbField;
        this.index = index;
        this.required = required;
        this.regex = regex;
    }
    // getters
}
