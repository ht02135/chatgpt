package simple.chatgpt.service.management;

import java.util.Map;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.util.PagedResult;

public interface UserManagementListMemberService {

	// ======= 5 CORE METHODS (on top) =======
	UserManagementListMemberPojo create(UserManagementListMemberPojo member);
	UserManagementListMemberPojo update(Long id, UserManagementListMemberPojo member);
	PagedResult<UserManagementListMemberPojo> search(Map<String, String> params);
	UserManagementListMemberPojo get(Long id);
	void delete(Long id);

	// ======= OTHER METHODS =======
    // ------------------ SEARCH / LIST ------------------
	PagedResult<UserManagementListMemberPojo> searchMembers(Map<String, Object> params);
	long countMembers(Map<String, Object> params);

    // ------------------ READ ------------------
    UserManagementListMemberPojo getMemberById(Map<String, Object> params);       // params should include "id"
    UserManagementListMemberPojo getMemberByUserName(Map<String, Object> params); // params should include "userName"

    // ------------------ CREATE ------------------
    UserManagementListMemberPojo createMember(Map<String, Object> params);        // params include "member"
    int batchCreateMembers(Map<String, Object> params);                            // params include "members"

    // ------------------ UPDATE ------------------
    UserManagementListMemberPojo updateMemberById(Map<String, Object> params);    // params include "id" and "member"
    UserManagementListMemberPojo updateMemberByUserName(Map<String, Object> params); // params include "userName" and "member"

    // ------------------ DELETE ------------------
    void deleteMemberById(Map<String, Object> params);                             // params include "id"
    void deleteMemberByUserName(Map<String, Object> params);                       // params include "userName"
    void deleteMembersByListId(Map<String, Object> params);                        // params include "listId"
}
