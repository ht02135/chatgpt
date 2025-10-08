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

    // ---------------------
    // Getters and Setters
    // ---------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

    public java.sql.Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.sql.Timestamp updatedAt) { this.updatedAt = updatedAt; }

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
