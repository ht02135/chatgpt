package simple.chatgpt.pojo.management;

import java.util.List;

import simple.chatgpt.pojo.management.jwt.JwtUser;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.validator.management.user.ValidManagementUser;

@ValidManagementUser
public class UserManagementPojo implements JwtUser {

    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(UserManagementPojo.class);

    private Long id;
    private String userName;
    private String userKey;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postCode;
    private String country;
    private java.sql.Timestamp createdAt;
    private java.sql.Timestamp updatedAt;

    // ---- Security / RoleGroup ----
    private List<RoleGroupManagementPojo> roleGroups;
    private String delimitRoleGroups;
    private boolean active;
    private boolean locked;
    private String lastLoginIp;
    private java.sql.Timestamp lastLoginAt;
    private String jwtSecretVersion;

    public UserManagementPojo() {
        logger.debug("UserManagementPojo constructor called");
    }

    // ---------------------
    // Getters and Setters
    // ---------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserKey() { return userKey; }
    public void setUserKey(String userKey) { this.userKey = userKey; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostCode() { return postCode; }
    public void setPostCode(String postCode) { this.postCode = postCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

    public java.sql.Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.sql.Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public List<RoleGroupManagementPojo> getRoleGroups() { return roleGroups; }
    public void setRoleGroups(List<RoleGroupManagementPojo> roleGroups) { this.roleGroups = roleGroups; }

    @Override
    public List<String> getRoleGroupRefs() {
        if (roleGroups == null || roleGroups.isEmpty()) {
            return List.of(); // empty list
        }
        return roleGroups.stream()
                .map(RoleGroupManagementPojo::getGroupName)
                .toList();
    }

    // ---- NEW GETTER/SETTER ----
    public String getDelimitRoleGroups() { return delimitRoleGroups; }
    public void setDelimitRoleGroups(String delimitRoleGroups) { this.delimitRoleGroups = delimitRoleGroups; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    @Override
	public boolean getActive() {
		return isActive();
	}

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
    @Override
	public boolean getLocked() {
		return isLocked();
	}

    public String getLastLoginIp() { return lastLoginIp; }
    public void setLastLoginIp(String lastLoginIp) { this.lastLoginIp = lastLoginIp; }

    public java.sql.Timestamp getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(java.sql.Timestamp lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public String getJwtSecretVersion() { return jwtSecretVersion; }
    public void setJwtSecretVersion(String jwtSecretVersion) { this.jwtSecretVersion = jwtSecretVersion; }


    @Override
    public String toString() {
        return "UserManagementPojo{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userKey='" + userKey + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                ", locked=" + locked +
                ", lastLoginIp='" + lastLoginIp + '\'' +
                ", lastLoginAt=" + lastLoginAt +
                ", jwtSecretVersion='" + jwtSecretVersion + '\'' +
                ", roleGroups=" + roleGroups +
                ", delimitRoleGroups='" + delimitRoleGroups + '\'' +
                '}';
    }
}
