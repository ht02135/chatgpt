package simple.chatgpt.service.management.security;

import java.util.Map;

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
	
    UserManagementRoleGroupMappingPojo insertUserRoleGroup(Map<String, Object> params);
    UserManagementRoleGroupMappingPojo findByUserIdAndRoleGroupId(Map<String, Object> params);
}
