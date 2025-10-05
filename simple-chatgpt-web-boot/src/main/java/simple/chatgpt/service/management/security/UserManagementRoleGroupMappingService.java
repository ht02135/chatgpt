package simple.chatgpt.service.management.security;

import java.util.List;

import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;

public interface UserManagementRoleGroupMappingService {

    // 🔎 LIST / SEARCH
    List<UserManagementRoleGroupMappingPojo> findAll();

    List<UserManagementRoleGroupMappingPojo> findByUserId(Long userId);

    List<UserManagementRoleGroupMappingPojo> findByRoleGroupId(Long roleGroupId);

    // ➕ CREATE
    UserManagementRoleGroupMappingPojo addUserToRoleGroup(UserManagementRoleGroupMappingPojo mapping);

    // 🗑 DELETE
    void removeMappingById(Long id);

    void removeMappingByUserAndGroup(Long userId, Long roleGroupId);
}
