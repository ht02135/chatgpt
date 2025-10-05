package simple.chatgpt.pojo.management.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RoleManagementPojo {

    private static final Logger logger = LogManager.getLogger(RoleManagementPojo.class);

    private Long id;
    private String roleName;
    private String description;
    private java.sql.Timestamp createdAt;
    private java.sql.Timestamp updatedAt;

    public RoleManagementPojo() {
        logger.debug("RoleManagementPojo constructor called");
    }

    public Long getId() {
        logger.debug("getId called");
        return id;
    }

    public void setId(Long id) {
        logger.debug("setId id={}", id);
        this.id = id;
    }

    public String getRoleName() {
        logger.debug("getRoleName called");
        return roleName;
    }

    public void setRoleName(String roleName) {
        logger.debug("setRoleName roleName={}", roleName);
        this.roleName = roleName;
    }

    public String getDescription() {
        logger.debug("getDescription called");
        return description;
    }

    public void setDescription(String description) {
        logger.debug("setDescription description={}", description);
        this.description = description;
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
        return "RoleManagementPojo{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
