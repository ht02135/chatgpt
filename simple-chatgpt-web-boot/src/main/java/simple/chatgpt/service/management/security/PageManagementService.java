package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.security.PageManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface PageManagementService {
	
	// ======= 5 CORE METHODS (on top) =======
	PageManagementPojo create(PageManagementPojo page);
	PageManagementPojo update(Long id, PageManagementPojo page);
	PagedResult<PageManagementPojo> search(Map<String, String> params);
	PageManagementPojo get(Long id);
	void delete(Long id);

	// ======= OTHER METHODS =======
	
	List<PageManagementPojo> getPageByParams(Map<String, Object> params);
	List<PageManagementPojo> getAll();
	PageManagementPojo getPageByUrlPattern(String urlPattern); // #{params.urlPattern}
	
	// String delimitRoleGroups
	public List<String> getRoleGroupNamesByUrlPattern(String urlPattern);
	public List<String> getRoleNamesByUrlPattern(String urlPattern);
	
}
