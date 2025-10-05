package simple.chatgpt.pojo.management;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.validator.management.user.ValidManagementUser;

@ValidManagementUser
public class UserManagementPojo {

    private static final Logger logger = LogManager.getLogger(UserManagementPojo.class);

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

    public Long getId() {
        logger.debug("getId called");
        return id;
    }

    public void setId(Long id) {
        logger.debug("setId id={}", id);
        this.id = id;
    }

    public String getUserName() {
        logger.debug("getUserName called");
        return userName;
    }

    public void setUserName(String userName) {
        logger.debug("setUserName userName={}", userName);
        this.userName = userName;
    }

    public String getUserKey() {
        logger.debug("getUserKey called");
        return userKey;
    }

    public void setUserKey(String userKey) {
        logger.debug("setUserKey userKey={}", userKey);
        this.userKey = userKey;
    }

    public String getPassword() {
        logger.debug("getPassword called");
        return password;
    }

    public void setPassword(String password) {
        logger.debug("setPassword password={}", password);
        this.password = password;
    }

    public String getFirstName() {
        logger.debug("getFirstName called");
        return firstName;
    }

    public void setFirstName(String firstName) {
        logger.debug("setFirstName firstName={}", firstName);
        this.firstName = firstName;
    }

    public String getLastName() {
        logger.debug("getLastName called");
        return lastName;
    }

    public void setLastName(String lastName) {
        logger.debug("setLastName lastName={}", lastName);
        this.lastName = lastName;
    }

    public String getEmail() {
        logger.debug("getEmail called");
        return email;
    }

    public void setEmail(String email) {
        logger.debug("setEmail email={}", email);
        this.email = email;
    }

    public String getAddressLine1() {
        logger.debug("getAddressLine1 called");
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        logger.debug("setAddressLine1 addressLine1={}", addressLine1);
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        logger.debug("getAddressLine2 called");
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        logger.debug("setAddressLine2 addressLine2={}", addressLine2);
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        logger.debug("getCity called");
        return city;
    }

    public void setCity(String city) {
        logger.debug("setCity city={}", city);
        this.city = city;
    }

    public String getState() {
        logger.debug("getState called");
        return state;
    }

    public void setState(String state) {
        logger.debug("setState state={}", state);
        this.state = state;
    }

    public String getPostCode() {
        logger.debug("getPostCode called");
        return postCode;
    }

    public void setPostCode(String postCode) {
        logger.debug("setPostCode postCode={}", postCode);
        this.postCode = postCode;
    }

    public String getCountry() {
        logger.debug("getCountry called");
        return country;
    }

    public void setCountry(String country) {
        logger.debug("setCountry country={}", country);
        this.country = country;
    }

    public java.sql.Timestamp getCreatedAt() {
        logger.debug("getCreatedAt called");
        return createdAt;
    }

    public void setCreatedAt(java.sql.Timestamp createdAt) {
        logger.debug("setCreatedAt createdAt={}", createdAt);
        this.createdAt = createdAt;
    }

    public java.sql.Timestamp getUpdatedAt() {
        logger.debug("getUpdatedAt called");
        return updatedAt;
    }

    public void setUpdatedAt(java.sql.Timestamp updatedAt) {
        logger.debug("setUpdatedAt updatedAt={}", updatedAt);
        this.updatedAt = updatedAt;
    }

    public List<RoleGroupManagementPojo> getRoleGroups() {
        logger.debug("getRoleGroups called");
        return roleGroups;
    }

    public void setRoleGroups(List<RoleGroupManagementPojo> roleGroups) {
        logger.debug("setRoleGroups roleGroups={}", roleGroups);
        this.roleGroups = roleGroups;
    }

    public boolean isActive() {
        logger.debug("isActive called");
        return active;
    }

    public void setActive(boolean active) {
        logger.debug("setActive active={}", active);
        this.active = active;
    }

    public boolean isLocked() {
        logger.debug("isLocked called");
        return locked;
    }

    public void setLocked(boolean locked) {
        logger.debug("setLocked locked={}", locked);
        this.locked = locked;
    }

    public String getLastLoginIp() {
        logger.debug("getLastLoginIp called");
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        logger.debug("setLastLoginIp lastLoginIp={}", lastLoginIp);
        this.lastLoginIp = lastLoginIp;
    }

    public java.sql.Timestamp getLastLoginAt() {
        logger.debug("getLastLoginAt called");
        return lastLoginAt;
    }

    public void setLastLoginAt(java.sql.Timestamp lastLoginAt) {
        logger.debug("setLastLoginAt lastLoginAt={}", lastLoginAt);
        this.lastLoginAt = lastLoginAt;
    }

    public String getJwtSecretVersion() {
        logger.debug("getJwtSecretVersion called");
        return jwtSecretVersion;
    }

    public void setJwtSecretVersion(String jwtSecretVersion) {
        logger.debug("setJwtSecretVersion jwtSecretVersion={}", jwtSecretVersion);
        this.jwtSecretVersion = jwtSecretVersion;
    }

    @Override
    public String toString() {
        return "UserManagementPojo{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userKey='" + userKey + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                ", locked=" + locked +
                ", lastLoginIp='" + lastLoginIp + '\'' +
                ", lastLoginAt=" + lastLoginAt +
                ", jwtSecretVersion='" + jwtSecretVersion + '\'' +
                ", roleGroups=" + roleGroups +
                '}';
    }
}
