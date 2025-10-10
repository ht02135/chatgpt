package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface RoleGroupManagementService {

	// ======= 5 CORE METHODS (on top) =======
	RoleGroupManagementPojo create(RoleGroupManagementPojo roleGroup);
	RoleGroupManagementPojo update(Long id, RoleGroupManagementPojo roleGroup);
	PagedResult<RoleGroupManagementPojo> search(Map<String, String> params);
	RoleGroupManagementPojo get(Long id);
	void delete(Long id);

	// ======= OTHER METHODS =======

	List<RoleGroupManagementPojo> getAll();
}
