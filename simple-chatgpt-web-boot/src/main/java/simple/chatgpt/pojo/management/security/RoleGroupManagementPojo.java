package simple.chatgpt.pojo.management.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RoleGroupManagementPojo {

    private static final Logger logger = LogManager.getLogger(RoleGroupManagementPojo.class);

    private Long id;
    private String groupName;
    private String description;
    private String createdAt;
    private String updatedAt;

    public RoleGroupManagementPojo() {
        logger.debug("RoleGroupManagementPojo constructor called");
    }

    public Long getId() {
        logger.debug("getId called");
        return id;
    }

    public void setId(Long id) {
        logger.debug("setId id={}", id);
        this.id = id;
    }

    public String getGroupName() {
        logger.debug("getGroupName called");
        return groupName;
    }

    public void setGroupName(String groupName) {
        logger.debug("setGroupName groupName={}", groupName);
        this.groupName = groupName;
    }

    public String getDescription() {
        logger.debug("getDescription called");
        return description;
    }

    public void setDescription(String description) {
        logger.debug("setDescription description={}", description);
        this.description = description;
    }

    public String getCreatedAt() {
        logger.debug("getCreatedAt called");
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        logger.debug("setCreatedAt createdAt={}", createdAt);
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        logger.debug("getUpdatedAt called");
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        logger.debug("setUpdatedAt updatedAt={}", updatedAt);
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "RoleGroupManagementPojo{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", description='" + description + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
