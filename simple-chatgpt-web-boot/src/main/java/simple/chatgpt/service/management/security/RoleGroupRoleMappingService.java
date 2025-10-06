package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;

public interface RoleGroupRoleMappingService {

    // ---------------- CREATE ----------------
    int insertMapping(Map<String, Object> params);

    // ? CREATE IF NOT EXISTS
    RoleGroupRoleMappingPojo addRoleToGroupIfNotExists(Map<String, Object> params); // params: roleGroupId, roleId

    // ---------------- DELETE ----------------
    int deleteMappingById(Map<String, Object> params);
    int deleteMappingByGroupAndRole(Map<String, Object> params);

    // ---------------- READ ----------------
    List<RoleGroupRoleMappingPojo> findAllMappings();
    List<RoleGroupRoleMappingPojo> findByRoleGroupId(Map<String, Object> params);
    List<RoleGroupRoleMappingPojo> findByRoleId(Map<String, Object> params);

    // ---------------- SEARCH / PAGINATION ----------------
    List<RoleGroupRoleMappingPojo> findMappings(Map<String, Object> params);
    List<RoleGroupRoleMappingPojo> searchMappings(Map<String, Object> params);

    // ---------------- COUNT ----------------
    long countMappings(Map<String, Object> params);
}
