package simple.chatgpt.config.management.security;

public class RoleConfig {
    private String name;
    private String description;

    public RoleConfig(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
