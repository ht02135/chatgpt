package simple.chatgpt.controller.management.security;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

public interface RoleGroupRoleMappingControllerApi {

    // ---------------- CREATE ----------------
    ResponseEntity<Response<RoleGroupRoleMappingPojo>> insertMapping(RoleGroupRoleMappingPojo mapping);

    ResponseEntity<Response<RoleGroupRoleMappingPojo>> addRoleToGroupIfNotExists(Long roleGroupId, Long roleId);

    // ---------------- DELETE ----------------
    ResponseEntity<Response<Void>> deleteMappingById(Long id);

    ResponseEntity<Response<Void>> deleteMappingByGroupAndRole(Long roleGroupId, Long roleId);

    // ---------------- READ ----------------
    ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findAllMappings();

    ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findByRoleGroupId(Long roleGroupId);

    ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findByRoleId(Long roleId);

    // ---------------- SEARCH / PAGINATION ----------------
    ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findMappings(Map<String, Object> params);

    ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> searchMappings(Map<String, Object> params);

    // ---------------- COUNT ----------------
    ResponseEntity<Response<Long>> countMappings(Map<String, Object> params);
}
