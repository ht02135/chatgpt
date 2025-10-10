package simple.chatgpt.service.management;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.util.PagedResult;

public interface UserManagementListMemberService {

	// ======= 5 CORE METHODS (on top) =======
	UserManagementListMemberPojo create(UserManagementListMemberPojo member);
	UserManagementListMemberPojo update(Long id, UserManagementListMemberPojo member);
	PagedResult<UserManagementListMemberPojo> search(Map<String, String> params);
	UserManagementListMemberPojo get(Long id);
	void delete(Long id);

	// ======= OTHER METHODS =======

	List<UserManagementListMemberPojo> getMembersByParams(Map<String, Object> params);
	List<UserManagementListMemberPojo> getMembersByListId(Long listId);	// #{params.listId}
	List<UserManagementListMemberPojo> getAll();
	
}
