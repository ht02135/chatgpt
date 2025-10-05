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

    public Long getId() {
        logger.debug("getId called");
        return id;
    }

    public void setId(Long id) {
        logger.debug("setId id={}", id);
        this.id = id;
    }

    public Long getUserId() {
        logger.debug("getUserId called");
        return userId;
    }

    public void setUserId(Long userId) {
        logger.debug("setUserId userId={}", userId);
        this.userId = userId;
    }

    public Long getRoleGroupId() {
        logger.debug("getRoleGroupId called");
        return roleGroupId;
    }

    public void setRoleGroupId(Long roleGroupId) {
        logger.debug("setRoleGroupId roleGroupId={}", roleGroupId);
        this.roleGroupId = roleGroupId;
    }

    public Timestamp getCreatedAt() {
        logger.debug("getCreatedAt called");
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        logger.debug("setCreatedAt createdAt={}", createdAt);
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        logger.debug("getUpdatedAt called");
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        logger.debug("setUpdatedAt updatedAt={}", updatedAt);
        this.updatedAt = updatedAt;
    }

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
