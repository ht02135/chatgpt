package simple.chatgpt.config.management;

import java.util.ArrayList;
import java.util.List;

public class RoleGroupConfig {
    private String name;
    private String description;
    private List<RoleRefConfig> roles = new ArrayList<>();

    public RoleGroupConfig(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addRole(RoleRefConfig roleRef) {
        roles.add(roleRef);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<RoleRefConfig> getRoles() {
        return roles;
    }
}
