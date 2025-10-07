package simple.chatgpt.controller.management.security;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

public interface RoleManagementControllerApi {

    // ---------------- CREATE ----------------
    ResponseEntity<Response<RoleManagementPojo>> insertRole(RoleManagementPojo role);

    // ---------------- UPDATE ----------------
    ResponseEntity<Response<RoleManagementPojo>> updateRole(RoleManagementPojo role, Long roleId, String roleName);

    // ---------------- DELETE ----------------
    ResponseEntity<Response<Void>> deleteRoleById(Long roleId);

    // ---------------- READ ----------------
    ResponseEntity<Response<RoleManagementPojo>> findRoleById(Long roleId);

    // ---------------- LIST / PAGINATION ----------------
    ResponseEntity<Response<PagedResult<RoleManagementPojo>>> findAllRoles();

    ResponseEntity<Response<PagedResult<RoleManagementPojo>>> getAllRoles();

    ResponseEntity<Response<PagedResult<RoleManagementPojo>>> findRoles(Map<String, Object> requestParams);

    ResponseEntity<Response<PagedResult<RoleManagementPojo>>> searchRoles(Map<String, Object> requestParams);

    // ---------------- COUNT ----------------
    ResponseEntity<Response<Long>> countRoles(Map<String, Object> requestParams);
}
