package simple.chatgpt.config.management.security;

public class UserConfig {
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
    private boolean active;
    private boolean locked;
    private String roleGroup;

    // 🔹 new field
    private String delimitRoleGroups;

    // 🔹 No-args constructor for XML loader or reflection
    public UserConfig() {}

    public UserConfig(String userName, String userKey, String password,
                      String firstName, String lastName, String email,
                      String addressLine1, String addressLine2,
                      String city, String state, String postCode, String country,
                      boolean active, boolean locked, String roleGroup,
                      String delimitRoleGroups) { // <-- updated constructor
        this.userName = userName;
        this.userKey = userKey;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.postCode = postCode;
        this.country = country;
        this.active = active;
        this.locked = locked;
        this.roleGroup = roleGroup;
        this.delimitRoleGroups = delimitRoleGroups;
    }

    // 🔹 Getters
    public String getUserName() { return userName; }
    public String getUserKey() { return userKey; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getAddressLine1() { return addressLine1; }
    public String getAddressLine2() { return addressLine2; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostCode() { return postCode; }
    public String getCountry() { return country; }
    public boolean isActive() { return active; }
    public boolean isLocked() { return locked; }
    public String getRoleGroup() { return roleGroup; }
    public String getDelimitRoleGroups() { return delimitRoleGroups; }

    // 🔹 Setters
    public void setRoleGroup(String roleGroup) { this.roleGroup = roleGroup; }
    public void setDelimitRoleGroups(String delimitRoleGroups) { this.delimitRoleGroups = delimitRoleGroups; }

    @Override
    public String toString() {
        return "UserConfig{" +
                "userName='" + userName + '\'' +
                ", roleGroup='" + roleGroup + '\'' +
                ", delimitRoleGroups='" + delimitRoleGroups + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
