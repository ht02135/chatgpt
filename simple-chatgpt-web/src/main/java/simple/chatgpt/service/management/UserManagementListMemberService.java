package simple.chatgpt.service.management;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;

public interface UserManagementListMemberService {

    // ------------------ SEARCH / LIST ------------------
    List<UserManagementListMemberPojo> searchMembers(Map<String, Object> params);
    long countMembers(Map<String, Object> params);

    // ------------------ READ ------------------
    UserManagementListMemberPojo getMemberById(Long id);
    UserManagementListMemberPojo getMemberByUserName(String userName);

    // ------------------ CREATE ------------------
    UserManagementListMemberPojo createMember(UserManagementListMemberPojo member);
    int batchCreateMembers(List<UserManagementListMemberPojo> members);

    // ------------------ UPDATE ------------------
    UserManagementListMemberPojo updateMemberById(Long id, UserManagementListMemberPojo member);
    UserManagementListMemberPojo updateMemberByUserName(String userName, UserManagementListMemberPojo member);

    // ------------------ DELETE ------------------
    void deleteMemberById(Long id);
    void deleteMemberByUserName(String userName);
    void deleteMembersByListId(Long listId);
}
