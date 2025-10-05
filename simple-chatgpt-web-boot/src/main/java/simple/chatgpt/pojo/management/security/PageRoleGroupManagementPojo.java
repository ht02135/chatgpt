package simple.chatgpt.pojo.management.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PageRoleGroupManagementPojo {

    private static final Logger logger = LogManager.getLogger(PageRoleGroupManagementPojo.class);

    private Long id;
    private String urlPattern;
    private RoleGroupManagementPojo roleGroup;
    private java.sql.Timestamp createdAt;
    private java.sql.Timestamp updatedAt;

    public PageRoleGroupManagementPojo() {
        logger.debug("PageRoleGroupManagementPojo constructor called");
    }

    public Long getId() {
        logger.debug("getId called");
        return id;
    }

    public void setId(Long id) {
        logger.debug("setId id={}", id);
        this.id = id;
    }

    public String getUrlPattern() {
        logger.debug("getUrlPattern called");
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        logger.debug("setUrlPattern urlPattern={}", urlPattern);
        this.urlPattern = urlPattern;
    }

    public RoleGroupManagementPojo getRoleGroup() {
        logger.debug("getRoleGroup called");
        return roleGroup;
    }

    public void setRoleGroup(RoleGroupManagementPojo roleGroup) {
        logger.debug("setRoleGroup roleGroup={}", roleGroup);
        this.roleGroup = roleGroup;
    }

    public java.sql.Timestamp getCreatedAt() {
        logger.debug("getCreatedAt called");
        return createdAt;
    }

    public void setCreatedAt(java.sql.Timestamp createdAt) {
        logger.debug("setCreatedAt createdAt={}", createdAt);
        this.createdAt = createdAt;
    }

    public java.sql.Timestamp getUpdatedAt() {
        logger.debug("getUpdatedAt called");
        return updatedAt;
    }

    public void setUpdatedAt(java.sql.Timestamp updatedAt) {
        logger.debug("setUpdatedAt updatedAt={}", updatedAt);
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
