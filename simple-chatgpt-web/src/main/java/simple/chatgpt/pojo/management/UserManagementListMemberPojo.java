package simple.chatgpt.pojo.management;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * POJO representing a member in a user management list.
 */
public class UserManagementListMemberPojo {
    private Long id;
    private Long listId;
    private String userName;
    private String userKey;      // 🔹 newly added
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
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public UserManagementListMemberPojo() {}

    // Full constructor
    public UserManagementListMemberPojo(Long id, Long listId, String userName, String userKey,
                                        String password, String firstName, String lastName, String email,
                                        String addressLine1, String addressLine2,
                                        String city, String state, String postCode, String country,
                                        Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.listId = listId;
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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getListId() { return listId; }
    public void setListId(Long listId) { this.listId = listId; }

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

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // toString
    @Override
    public String toString() {
        return "UserManagementListMemberPojo{" +
                "id=" + id +
                ", listId=" + listId +
                ", userName='" + userName + '\'' +
                ", userKey='" + userKey + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    // equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserManagementListMemberPojo)) return false;
        UserManagementListMemberPojo that = (UserManagementListMemberPojo) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(listId, that.listId) &&
               Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, listId, email);
    }
}
