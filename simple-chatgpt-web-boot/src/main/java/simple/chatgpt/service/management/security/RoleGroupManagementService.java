package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface RoleGroupManagementService {
	
	String ROOT_ROLE_GROUP = "ROOT_ROLE_GROUP"; // constant added
	String ADMIN_ROLE_GROUP = "ADMIN_ROLE_GROUP"; // constant added
	String USER_ROLE_GROUP = "USER_ROLE_GROUP"; // constant added

	// ======= 5 CORE METHODS (on top) =======
	RoleGroupManagementPojo create(RoleGroupManagementPojo roleGroup);
	RoleGroupManagementPojo update(Long id, RoleGroupManagementPojo roleGroup);
	PagedResult<RoleGroupManagementPojo> search(Map<String, String> params);
	RoleGroupManagementPojo get(Long id);
	void delete(Long id);

	// ======= OTHER METHODS =======

	public List<RoleGroupManagementPojo> getRoleGroupByParams(Map<String, Object> params);
	public List<RoleGroupManagementPojo> getAll();
	public RoleGroupManagementPojo getRoleGroupByGroupName(String groupName); // #{params.groupName}
	
	// String delimitRoles
	public List<String> getRoleNamesByGroupName(String groupName);
}
