package simple.chatgpt.config.management.security;

public class PageConfig {

    private String urlPattern;
    private String delimitRoleGroups;

    // 🔹 No-args constructor for XML loader or reflection
    public PageConfig() {}

    public PageConfig(String urlPattern, String delimitRoleGroups) {
        this.urlPattern = urlPattern;
        this.delimitRoleGroups = delimitRoleGroups;
    }

    // 🔹 Getters
    public String getUrlPattern() {
        return urlPattern;
    }

    public String getDelimitRoleGroups() {
        return delimitRoleGroups;
    }

    // 🔹 Setters
    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public void setDelimitRoleGroups(String delimitRoleGroups) {
        this.delimitRoleGroups = delimitRoleGroups;
    }

    @Override
    public String toString() {
        return "PageConfig{" +
                "urlPattern='" + urlPattern + '\'' +
                ", delimitRoleGroups='" + delimitRoleGroups + '\'' +
                '}';
    }
}
