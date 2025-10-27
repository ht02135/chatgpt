package simple.chatgpt.config.management;

import java.util.ArrayList;
import java.util.List;

public class FormConfig {
    private String id;
    private List<FieldConfig> fields = new ArrayList<>();

    public FormConfig() {}
    public FormConfig(String id) { this.id = id; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<FieldConfig> getFields() { return fields; }
    public void setFields(List<FieldConfig> fields) { this.fields = fields; }

    public void addField(FieldConfig field) { this.fields.add(field); }
}