package simple.chatgpt.config.management;

public class ActionConfig {
    private String name;
    private String label;
    private boolean visible;
    private String jsMethod;

    public ActionConfig(String name, String label, boolean visible, String jsMethod) {
        this.name = name;
        this.label = label;
        this.visible = visible;
        this.jsMethod = jsMethod;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getJsMethod() {
        return jsMethod;
    }
}