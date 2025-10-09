package simple.chatgpt.service.management;

import java.util.Map;
import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.util.PagedResult;
import java.util.List;

public interface UserManagementService {

    // ======= 5 CORE METHODS (on top) =======
    UserManagementPojo create(UserManagementPojo user);
    UserManagementPojo update(Long id, UserManagementPojo user);
    PagedResult<UserManagementPojo> search(Map<String, String> params);
    UserManagementPojo get(Long id);
    void delete(Long id);

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
