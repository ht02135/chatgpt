package simple.chatgpt.controller.management.security;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

public interface RoleGroupManagementControllerApi {

    // ---------------- CREATE ----------------
    ResponseEntity<Response<RoleGroupManagementPojo>> insertRoleGroup(RoleGroupManagementPojo group);

    // ---------------- UPDATE ----------------
    ResponseEntity<Response<RoleGroupManagementPojo>> updateRoleGroup(Map<String, Object> params);

    // ---------------- DELETE ----------------
    ResponseEntity<Response<Void>> deleteRoleGroupById(Long roleGroupId);

    ResponseEntity<Response<Void>> deleteRoleGroupByName(String groupName);

    // ---------------- READ ----------------
    ResponseEntity<Response<RoleGroupManagementPojo>> findRoleGroupById(Long roleGroupId);

    ResponseEntity<Response<RoleGroupManagementPojo>> findRoleGroupByName(String groupName);

    // ---------------- LIST ALL ----------------
    ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> findAllRoleGroups();

    ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> getAllRoleGroups();

    // ---------------- SEARCH / PAGINATION ----------------
    ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> findRoleGroups(Map<String, Object> params);

    ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> searchRoleGroups(Map<String, Object> params);

    // ---------------- COUNT ----------------
    ResponseEntity<Response<Long>> countRoleGroups(Map<String, Object> params);
}
