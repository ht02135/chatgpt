package simple.chatgpt.pojo.management.security;

import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RoleGroupRoleMappingPojo {

    private static final Logger logger = LogManager.getLogger(RoleGroupRoleMappingPojo.class);

    private Long id;
    private Long roleGroupId;
    private Long roleId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public RoleGroupRoleMappingPojo() {
        logger.debug("RoleGroupRoleMappingPojo constructor called");
    }

    public Long getId() {
        logger.debug("getId called");
        return id;
    }

    public void setId(Long id) {
        logger.debug("setId id={}", id);
        this.id = id;
    }

    public Long getRoleGroupId() {
        logger.debug("getRoleGroupId called");
        return roleGroupId;
    }

    public void setRoleGroupId(Long roleGroupId) {
        logger.debug("setRoleGroupId roleGroupId={}", roleGroupId);
        this.roleGroupId = roleGroupId;
    }

    public Long getRoleId() {
        logger.debug("getRoleId called");
        return roleId;
    }

    public void setRoleId(Long roleId) {
        logger.debug("setRoleId roleId={}", roleId);
        this.roleId = roleId;
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
        return "RoleGroupRoleMappingPojo{" +
                "id=" + id +
                ", roleGroupId=" + roleGroupId +
                ", roleId=" + roleId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
