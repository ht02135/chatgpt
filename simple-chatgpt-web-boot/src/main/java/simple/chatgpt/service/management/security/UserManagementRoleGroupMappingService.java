package simple.chatgpt.service.management.security;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.util.PagedResult;

public interface UserManagementRoleGroupMappingService {

	// ======= 5 CORE METHODS (on top) =======
	UserManagementRoleGroupMappingPojo create(UserManagementRoleGroupMappingPojo mapping);
	UserManagementRoleGroupMappingPojo update(Long id, UserManagementRoleGroupMappingPojo mapping);
	PagedResult<UserManagementRoleGroupMappingPojo> search(Map<String, String> params);
	UserManagementRoleGroupMappingPojo get(Long id);
	void delete(Long id);

	// ======= OTHER METHODS =======
    // ---------------- CREATE ----------------
    UserManagementRoleGroupMappingPojo insertUserRoleGroup(Map<String, Object> params);

    // ---------------- UPDATE ----------------
    UserManagementRoleGroupMappingPojo updateUserRoleGroup(Map<String, Object> params);

    // ---------------- DELETE ----------------
    void deleteUserRoleGroupById(Map<String, Object> params);
    void deleteUserRoleGroupByUserAndGroup(Map<String, Object> params);

    // ---------------- READ ----------------
    UserManagementRoleGroupMappingPojo findByUserIdAndRoleGroupId(Map<String, Object> params);
    UserManagementRoleGroupMappingPojo findById(Map<String, Object> params);

    PagedResult<UserManagementRoleGroupMappingPojo> findAllUserRoleGroups();
    PagedResult<UserManagementRoleGroupMappingPojo> findByUserId(Map<String, Object> params);
    PagedResult<UserManagementRoleGroupMappingPojo> findByRoleGroupId(Map<String, Object> params);

    // ---------------- SEARCH / PAGINATION ----------------
    PagedResult<UserManagementRoleGroupMappingPojo> findUserRoleGroups(Map<String, Object> params);
    PagedResult<UserManagementRoleGroupMappingPojo> searchUserRoleGroups(Map<String, Object> params);

    // ---------------- COUNT ----------------
    long countUserRoleGroups(Map<String, Object> params);
}
