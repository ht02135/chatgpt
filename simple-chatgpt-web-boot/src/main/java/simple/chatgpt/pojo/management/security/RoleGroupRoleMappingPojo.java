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
    private String roleGroupName;
    private String roleName;

    public RoleGroupRoleMappingPojo() {
        logger.debug("RoleGroupRoleMappingPojo constructor called");
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRoleGroupId() { return roleGroupId; }
    public void setRoleGroupId(Long roleGroupId) { this.roleGroupId = roleGroupId; }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getRoleGroupName() { return roleGroupName; }
    public void setRoleGroupName(String roleGroupName) { this.roleGroupName = roleGroupName; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    @Override
    public String toString() {
        return "RoleGroupRoleMappingPojo{" +
                "id=" + id +
                ", roleGroupId=" + roleGroupId +
                ", roleGroupName='" + roleGroupName + '\'' +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
