package simple.chatgpt.config.management.security;

public class PageRoleGroupConfig {
    private String urlPattern;
    private String roleGroup;

    public PageRoleGroupConfig(String urlPattern, String roleGroup) {
        this.urlPattern = urlPattern;
        this.roleGroup = roleGroup;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public String getRoleGroup() {
        return roleGroup;
    }
}
