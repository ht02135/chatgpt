package simple.chatgpt.pojo.management.security;

import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import simple.chatgpt.pojo.management.jwt.JwtRoleGroup;

public class PageRoleGroupManagementPojo implements JwtRoleGroup {

    private static final Logger logger = LogManager.getLogger(PageRoleGroupManagementPojo.class);

    private Long id;
    private String urlPattern;
    /*
    Hung : DONT REMOVE
    Keeping private RoleGroupManagementPojo roleGroup; lets the 
    PageRoleGroupManagementPojo directly hold the full role group object instead 
    of just its ID, which makes the code more object-oriented, easier to read, 
    simplifies logging/debugging, reduces extra DB lookups, and allows direct 
    access to role group details without additional service calls.
    ///////////////////
    private Long roleGroupId; // stores only the foreign key to the role 
    group, keeps POJO lightweight, aligns directly with DB schema, avoids 
    unnecessary object nesting, and simplifies persistence and mapping
    */
    private RoleGroupManagementPojo roleGroup; // Nested role group
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public PageRoleGroupManagementPojo() {
        logger.debug("PageRoleGroupManagementPojo constructor called");
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrlPattern() { return urlPattern; }
    public void setUrlPattern(String urlPattern) { this.urlPattern = urlPattern; }

    public RoleGroupManagementPojo getRoleGroup() { return roleGroup; }
    public void setRoleGroup(RoleGroupManagementPojo roleGroup) { this.roleGroup = roleGroup; }
	@Override
	public String getRoleGroupRef() {
	    return getRoleGroup().getGroupName(); // e.g., "ADMIN_ROLE_GROUP"
	}
	
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

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
