package simple.chatgpt.pojo.management;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * POJO representing a user management list.
 */
public class UserManagementListPojo {
    private Long id;
    private String userListName;
    private String filePath;          // canonical storage location (/data/management/user_lists/{id}.ext)
    private String originalFileName;  // original uploaded file name (e.g. test_user_lists_1.csv)
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public UserManagementListPojo() {}

    // Constructor with all fields
    public UserManagementListPojo(Long id, String userListName, String filePath,
                                  String originalFileName, String description,
                                  Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userListName = userListName;
        this.filePath = filePath;
        this.originalFileName = originalFileName;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserListName() { return userListName; }
    public void setUserListName(String userListName) { this.userListName = userListName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "UserManagementListPojo{" +
                "id=" + id +
                ", userListName='" + userListName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", originalFileName='" + originalFileName + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserManagementListPojo)) return false;
        UserManagementListPojo that = (UserManagementListPojo) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(userListName, that.userListName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userListName);
    }
}
