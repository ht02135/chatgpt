package simple.chatgpt.service.management;

import java.util.Map;
import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;
import java.util.List;

public interface UserManagementService {

    // ======= 5 CORE METHODS (on top) =======
    UserManagementPojo create(UserManagementPojo user);
    UserManagementPojo update(Long id, UserManagementPojo user);
    PagedResult<UserManagementPojo> search(Map<String, String> params);
    UserManagementPojo get(Long id);
    void delete(Long id);
    
    // ======= OTHER METHODS =======
    
	public List<UserManagementPojo> getUserByParams(Map<String, Object> params);
	public List<UserManagementPojo> getAll();
	public UserManagementPojo getUserByUserName(String userName); // #{params.userName}

	// String delimitRoleGroups
	public List<String> getRoleGroupNamesByUserName(String userName);
	public List<String> getRoleNamesByUserName(String userName);
	
}
