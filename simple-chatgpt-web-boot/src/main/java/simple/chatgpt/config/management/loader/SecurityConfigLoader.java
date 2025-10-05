package simple.chatgpt.config.management.loader;

import java.util.ArrayList;
import java.util.List;

import simple.chatgpt.config.management.PageRoleGroupConfig;
import simple.chatgpt.config.management.RoleConfig;
import simple.chatgpt.config.management.RoleGroupConfig;

public class SecurityConfigLoader {

    private List<RoleConfig> roles = new ArrayList<>();
    private List<RoleGroupConfig> roleGroups = new ArrayList<>();
    private List<PageRoleGroupConfig> pageRoleGroups = new ArrayList<>();

    public void addRole(RoleConfig role) {
        roles.add(role);
    }

    public void addRoleGroup(RoleGroupConfig roleGroup) {
        roleGroups.add(roleGroup);
    }

    public void addPageRoleGroup(PageRoleGroupConfig pageRoleGroup) {
        pageRoleGroups.add(pageRoleGroup);
    }

    public List<RoleConfig> getRoles() {
        return roles;
    }

    public List<RoleGroupConfig> getRoleGroups() {
        return roleGroups;
    }

    public List<PageRoleGroupConfig> getPageRoleGroups() {
        return pageRoleGroups;
    }
}
