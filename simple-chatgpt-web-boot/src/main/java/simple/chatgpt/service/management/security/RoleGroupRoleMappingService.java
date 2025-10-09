package simple.chatgpt.service.management.security;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.util.PagedResult;

public interface RoleGroupRoleMappingService {

    // ---------------- CREATE ----------------
	RoleGroupRoleMappingPojo insertMapping(Map<String, Object> params);

    // ✅ Create if not exists
    RoleGroupRoleMappingPojo addRoleToGroupIfNotExists(Map<String, Object> params); // roleGroupId, roleId

    // ---------------- DELETE ----------------
    void deleteMappingById(Map<String, Object> params);
    void deleteMappingByGroupAndRole(Map<String, Object> params);

    // ---------------- READ ----------------
    RoleGroupRoleMappingPojo findById(Map<String, Object> params);
    PagedResult<RoleGroupRoleMappingPojo> findAllMappings();
    PagedResult<RoleGroupRoleMappingPojo> findByRoleGroupId(Map<String, Object> params);
    PagedResult<RoleGroupRoleMappingPojo> findByRoleId(Map<String, Object> params);

    // ---------------- SEARCH / PAGINATION ----------------
    PagedResult<RoleGroupRoleMappingPojo> findMappings(Map<String, Object> params);
    PagedResult<RoleGroupRoleMappingPojo> searchMappings(Map<String, Object> params);

    // ---------------- COUNT ----------------
    long countMappings(Map<String, Object> params);
}
