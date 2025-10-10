package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface PageRoleGroupManagementService {
	
	// ======= 5 CORE METHODS (on top) =======
	PageRoleGroupManagementPojo create(PageRoleGroupManagementPojo pageRoleGroup);
	PageRoleGroupManagementPojo update(Long id, PageRoleGroupManagementPojo pageRoleGroup);
	PagedResult<PageRoleGroupManagementPojo> search(Map<String, String> params);
	PageRoleGroupManagementPojo get(Long id);
	void delete(Long id);

	// ======= OTHER METHODS =======
	
	public List<PageRoleGroupManagementPojo> getMappingsByParams(Map<String, Object> params);
	public List<PageRoleGroupManagementPojo> getMappingsByUrlPattern(String urlPattern); //#{params.urlPattern}
	public List<PageRoleGroupManagementPojo> getAll();
}
