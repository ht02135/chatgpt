package simple.chatgpt.pojo.management.security;

import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PageManagementPojo {

    private static final Logger logger = LogManager.getLogger(PageManagementPojo.class);

    private Long id;
    private String urlPattern;
    private String delimitRoleGroups;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public PageManagementPojo() {
        logger.debug("PageManagementPojo constructor called");
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getDelimitRoleGroups() {
        return delimitRoleGroups;
    }

    public void setDelimitRoleGroups(String delimitRoleGroups) {
        this.delimitRoleGroups = delimitRoleGroups;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "PageManagementPojo{" +
                "id=" + id +
                ", urlPattern='" + urlPattern + '\'' +
                ", delimitRoleGroups='" + delimitRoleGroups + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
