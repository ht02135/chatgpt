package simple.chatgpt.pojo.management.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RoleGroupManagementPojo {

    private static final Logger logger = LogManager.getLogger(RoleGroupManagementPojo.class);

    private Long id;
    private String groupName;

    public RoleGroupManagementPojo() {
        logger.debug("RoleGroupManagementPojo constructor called");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        logger.debug("setId id={}", id);
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        logger.debug("setGroupName groupName={}", groupName);
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "RoleGroupManagementPojo{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}
