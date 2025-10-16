package simple.chatgpt.pojo.management.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RoleGroupManagementPojo {

    private static final Logger logger = LogManager.getLogger(RoleGroupManagementPojo.class);

    private Long id;
    private String groupName;
    private String description;
    private String delimitRoles;
    private String createdAt;
    private String updatedAt;

    public RoleGroupManagementPojo() {
        logger.debug("RoleGroupManagementPojo constructor called");
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDelimitRoles() { return delimitRoles; }
    public void setDelimitRoles(String delimitRoles) { this.delimitRoles = delimitRoles; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "RoleGroupManagementPojo{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", description='" + description + '\'' +
                ", delimitRoles='" + delimitRoles + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
