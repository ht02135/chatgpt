package simple.chatgpt.service.management.security;

import java.util.Map;

import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface RoleManagementService {

    // ---------------- CREATE ----------------
    RoleManagementPojo insertRole(Map<String, Object> params); // matches mapper.insertRole

    // ---------------- UPDATE ----------------
    RoleManagementPojo updateRole(Map<String, Object> params); // matches mapper.updateRole

    // ---------------- DELETE ----------------
    void deleteRoleById(Map<String, Object> params); // matches mapper.deleteRoleById

    // ---------------- READ ----------------
    RoleManagementPojo findRoleById(Map<String, Object> params); // matches mapper.findRoleById

    // ---------------- FETCH ALL ----------------
    PagedResult<RoleManagementPojo> findAllRoles(); // matches mapper.findAllRoles
    PagedResult<RoleManagementPojo> getAllRoles();  // matches mapper.getAllRoles

    // ---------------- SEARCH / PAGINATION ----------------
    PagedResult<RoleManagementPojo> findRoles(Map<String, Object> params);   // matches mapper.findRoles
    PagedResult<RoleManagementPojo> searchRoles(Map<String, Object> params); // matches mapper.searchRoles
    
    // ---------------- COUNT ----------------
    long countRoles(Map<String, Object> params); // matches mapper.countRoles
}
