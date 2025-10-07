package simple.chatgpt.mapper.management;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.UserManagementPojo;

public interface UserManagementMapper {

    // 🔎 SEARCH / LIST
    List<UserManagementPojo> findUsers(@Param("params") Map<String, Object> params);

    long countUsers(@Param("params") Map<String, Object> params);

    // 📖 READ
    UserManagementPojo findById(@Param("id") Long id);

    UserManagementPojo findByUserName(@Param("userName") String userName);

    UserManagementPojo findByUserKey(@Param("userKey") String userKey);

    // ➕ CREATE
    void insertUser(@Param("user") UserManagementPojo user);

    // ✏️ UPDATE
    void updateUser(@Param("user") UserManagementPojo user);

    void updateUserByUserName(@Param("user") UserManagementPojo user);

    void updateUserByUserKey(@Param("user") UserManagementPojo user);

    // 🗑 DELETE
    void deleteById(@Param("id") Long id);

    void deleteByUserName(@Param("userName") String userName);

    void deleteByUserKey(@Param("userKey") String userKey);
}
