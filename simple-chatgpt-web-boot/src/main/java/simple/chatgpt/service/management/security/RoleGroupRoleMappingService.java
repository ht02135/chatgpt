package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.util.PagedResult;

public interface RoleGroupRoleMappingService {
	// ======= 5 CORE METHODS (on top) =======
	RoleGroupRoleMappingPojo create(RoleGroupRoleMappingPojo mapping);
	RoleGroupRoleMappingPojo update(Long id, RoleGroupRoleMappingPojo mapping);
	PagedResult<RoleGroupRoleMappingPojo> search(Map<String, String> params);
	RoleGroupRoleMappingPojo get(Long id);
	void delete(Long id);

	// ======= OTHER METHODS =======

	public List<RoleGroupRoleMappingPojo> getMappingsByParams(Map<String, Object> params);
	public List<RoleGroupRoleMappingPojo> getMappingsByRoleGroupId(Long roleGroupId); // #{params.roleGroupId}
	List<RoleGroupRoleMappingPojo> getAll();
	
	public void deleteByRoleGroupId(Long roleGroupId);
}
