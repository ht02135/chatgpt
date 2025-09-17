package simple.chatgpt.service.management;

public interface UserManagementService {

    // 🔎 LIST / SEARCH
    PagedResult<UserManagementPojo> searchUsers(Map<String, String> params);

    // 📖 READ
    UserManagementPojo getUserById(Long id);

    UserManagementPojo getByUserName(String userName);

    UserManagementPojo getByUserKey(String userKey);

    // ➕ CREATE
    UserManagementPojo createUser(UserManagementPojo user);

    // ✏️ UPDATE
    UserManagementPojo updateUserById(Long id, UserManagementPojo user);

    UserManagementPojo updateUserByUserName(String userName, UserManagementPojo user);

    UserManagementPojo updateUserByUserKey(String userKey, UserManagementPojo user);

    // 🗑 DELETE
    void deleteUserById(Long id);

    void deleteUserByUserName(String userName);

    void deleteUserByUserKey(String userKey);
}
