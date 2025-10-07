package simple.chatgpt.controller.management.security;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

public interface UserRoleGroupManagementControllerApi {

    // ---------------- CREATE ----------------
    ResponseEntity<Response<UserManagementRoleGroupMappingPojo>> insertUserRoleGroup(Long userId, Long roleGroupId);

    // ---------------- UPDATE ----------------
    ResponseEntity<Response<UserManagementRoleGroupMappingPojo>> updateUserRoleGroup(Long id, Long userId, Long roleGroupId);

    // ---------------- DELETE ----------------
    ResponseEntity<Response<Void>> deleteUserRoleGroupById(Long id);

    ResponseEntity<Response<Void>> deleteUserRoleGroupByUserAndGroup(Long userId, Long roleGroupId);

    // ---------------- READ ----------------
    ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findAllUserRoleGroups();

    ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findByUserId(Long userId);

    ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findByRoleGroupId(Long roleGroupId);

    // ---------------- SEARCH / PAGINATION ----------------
    ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findUserRoleGroups(Map<String, Object> params);

    ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> searchUserRoleGroups(Map<String, Object> params);

    // ---------------- COUNT ----------------
    ResponseEntity<Response<Long>> countUserRoleGroups(Map<String, Object> params);
}
