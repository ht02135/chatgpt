package simple.chatgpt.controller.management;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

public interface UserManagementListMemberControllerApi {

    // ➕ CREATE MEMBER
    ResponseEntity<Response<UserManagementListMemberPojo>> createMember(UserManagementListMemberPojo member);

    // 📖 GET MEMBER BY ID
    ResponseEntity<Response<UserManagementListMemberPojo>> getMemberById(Long memberId);

    // 📝 UPDATE MEMBER
    ResponseEntity<Response<UserManagementListMemberPojo>> updateMemberById(Long memberId, UserManagementListMemberPojo member);

    // 🗑 DELETE MEMBER
    ResponseEntity<Response<Void>> deleteMemberById(Long memberId);

    // 🔍 SEARCH MEMBERS
    ResponseEntity<Response<PagedResult<UserManagementListMemberPojo>>> searchMembers(Map<String, Object> params);

    // ------------------ COUNT MEMBERS ------------------
    ResponseEntity<Response<Long>> countMembers(Map<String, Object> params);
}
