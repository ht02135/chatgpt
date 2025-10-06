package simple.chatgpt.service.management.security;

import java.util.Map;

import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.util.PagedResult;

public interface RoleGroupRoleMappingService {

    // ---------------- CREATE ----------------
    int insertMapping(Map<String, Object> params);

    // ✅ Create if not exists
    RoleGroupRoleMappingPojo addRoleToGroupIfNotExists(Map<String, Object> params); // roleGroupId, roleId

    // ---------------- DELETE ----------------
    int deleteMappingById(Map<String, Object> params);
    int deleteMappingByGroupAndRole(Map<String, Object> params);

    // ---------------- READ ----------------
    PagedResult<RoleGroupRoleMappingPojo> findAllMappings();
    PagedResult<RoleGroupRoleMappingPojo> findByRoleGroupId(Map<String, Object> params);
    PagedResult<RoleGroupRoleMappingPojo> findByRoleId(Map<String, Object> params);

    // ---------------- SEARCH / PAGINATION ----------------
    PagedResult<RoleGroupRoleMappingPojo> findMappings(Map<String, Object> params);
    PagedResult<RoleGroupRoleMappingPojo> searchMappings(Map<String, Object> params);

    // ---------------- COUNT ----------------
    long countMappings(Map<String, Object> params);
}
