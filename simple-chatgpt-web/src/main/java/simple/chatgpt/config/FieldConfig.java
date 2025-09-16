package simple.chatgpt.config;

public class FieldConfig {
    private String name;
    private String label;
    private boolean visible;
    private boolean required;
    private boolean editable;
    private String regex;           // existing
    private String validatorsId;    // NEW: reference to validator group

    public FieldConfig() {}

    public FieldConfig(String name, String label, boolean visible, boolean required, boolean editable, String regex, String validatorsId) {
        this.name = name;
        this.label = label;
        this.visible = visible;
        this.required = required;
        this.editable = editable;
        this.regex = regex;
        this.validatorsId = validatorsId;
    }

    // getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }

    public boolean isEditable() { return editable; }
    public void setEditable(boolean editable) { this.editable = editable; }

    public String getRegex() { return regex; }
    public void setRegex(String regex) { this.regex = regex; }

    public String getValidatorsId() { return validatorsId; }
    public void setValidatorsId(String validatorsId) { this.validatorsId = validatorsId; }
}
