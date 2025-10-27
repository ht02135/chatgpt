package simple.chatgpt.config.management.security;

import java.util.ArrayList;
import java.util.List;

public class RoleGroupConfig {
    private String name;
    private String description;
    private List<RoleRefConfig> roles = new ArrayList<>();
    private String delimitRoles; // <-- new field

    // Updated constructor to include delimitRoles
    public RoleGroupConfig(String name, String description, String delimitRoles) {
        this.name = name;
        this.description = description;
        this.delimitRoles = delimitRoles;
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

    public String getDelimitRoles() {
        return delimitRoles;
    }

    public void setDelimitRoles(String delimitRoles) {
        this.delimitRoles = delimitRoles;
    }
}
