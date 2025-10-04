package simple.chatgpt.config;

import java.util.ArrayList;
import java.util.List;

public class FieldConfig {

    private String name;
    private String label;
    private boolean visible;
    private boolean required;
    private boolean editable;
    private String regex;
    private List<String> validators;

    /**
     * Constructor supporting multiple validators (List<String>)
     */
    public FieldConfig(String name, String label, boolean visible, boolean required, boolean editable, String regex, List<String> validators) {
        this.name = name;
        this.label = label;
        this.visible = visible;
        this.required = required;
        this.editable = editable;
        this.regex = regex;
        this.validators = validators != null ? validators : new ArrayList<>();
    }

    /**
     * Constructor for backward compatibility with single validator (String)
     */
    public FieldConfig(String name, String label, boolean visible, boolean required, boolean editable, String regex, String validator) {
        this(name, label, visible, required, editable, regex, validator != null ? List.of(validator) : new ArrayList<>());
    }

    // Getters and setters
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

    public List<String> getValidators() { return validators; }
    public void setValidators(List<String> validators) { this.validators = validators; }

    @Override
    public String toString() {
        return "FieldConfig{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", visible=" + visible +
                ", required=" + required +
                ", editable=" + editable +
                ", regex='" + regex + '\'' +
                ", validators=" + validators +
                '}';
    }
}
