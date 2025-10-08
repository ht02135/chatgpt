package simple.chatgpt.pojo.management.security;

import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserManagementRoleGroupMappingPojo {

    private static final Logger logger = LogManager.getLogger(UserManagementRoleGroupMappingPojo.class);

    private Long id;
    private Long userId;
    private Long roleGroupId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public UserManagementRoleGroupMappingPojo() {
        logger.debug("UserManagementRoleGroupMappingPojo constructor called");
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getRoleGroupId() { return roleGroupId; }
    public void setRoleGroupId(Long roleGroupId) { this.roleGroupId = roleGroupId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "UserManagementRoleGroupMappingPojo{" +
                "id=" + id +
                ", userId=" + userId +
                ", roleGroupId=" + roleGroupId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
