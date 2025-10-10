package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface RoleManagementService {
	
	// ======= 5 CORE METHODS (on top) =======
	RoleManagementPojo create(RoleManagementPojo role);
	RoleManagementPojo update(Long id, RoleManagementPojo role);
	PagedResult<RoleManagementPojo> search(Map<String, String> params);
	RoleManagementPojo get(Long id);
	void delete(Long id);

	// ======= OTHER METHODS =======
	
	List<RoleManagementPojo> getAll();
}
