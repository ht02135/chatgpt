package simple.chatgpt.service.management.security;

import java.util.Map;

import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.util.PagedResult;

public interface UserManagementRoleGroupMappingService {

    // ---------------- CREATE ----------------
    UserManagementRoleGroupMappingPojo insertUserRoleGroup(Map<String, Object> params);

    // ---------------- UPDATE ----------------
    UserManagementRoleGroupMappingPojo updateUserRoleGroup(Map<String, Object> params);

    // ---------------- DELETE ----------------
    void deleteUserRoleGroupById(Map<String, Object> params);
    void deleteUserRoleGroupByUserAndGroup(Map<String, Object> params);

    // ---------------- READ ----------------
    PagedResult<UserManagementRoleGroupMappingPojo> findAllUserRoleGroups();
    PagedResult<UserManagementRoleGroupMappingPojo> findByUserId(Map<String, Object> params);
    PagedResult<UserManagementRoleGroupMappingPojo> findByRoleGroupId(Map<String, Object> params);

    // ---------------- SEARCH / PAGINATION ----------------
    PagedResult<UserManagementRoleGroupMappingPojo> findUserRoleGroups(Map<String, Object> params);
    PagedResult<UserManagementRoleGroupMappingPojo> searchUserRoleGroups(Map<String, Object> params);

    // ---------------- COUNT ----------------
    long countUserRoleGroups(Map<String, Object> params);
}
