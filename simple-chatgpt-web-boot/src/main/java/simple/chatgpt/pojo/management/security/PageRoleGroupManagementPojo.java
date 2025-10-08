package simple.chatgpt.pojo.management.security;

import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PageRoleGroupManagementPojo {

    private static final Logger logger = LogManager.getLogger(PageRoleGroupManagementPojo.class);

    private Long id;
    private String urlPattern;
    private RoleGroupManagementPojo roleGroup; // Nested role group
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public PageRoleGroupManagementPojo() {
        logger.debug("PageRoleGroupManagementPojo constructor called");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        logger.debug("setId id={}", id);
        this.id = id;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        logger.debug("setUrlPattern urlPattern={}", urlPattern);
        this.urlPattern = urlPattern;
    }

    public RoleGroupManagementPojo getRoleGroup() {
        return roleGroup;
    }

    public void setRoleGroup(RoleGroupManagementPojo roleGroup) {
        logger.debug("setRoleGroup roleGroup={}", roleGroup);
        this.roleGroup = roleGroup;
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
        return "PageRoleGroupManagementPojo{" +
                "id=" + id +
                ", urlPattern='" + urlPattern + '\'' +
                ", roleGroup=" + roleGroup +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
