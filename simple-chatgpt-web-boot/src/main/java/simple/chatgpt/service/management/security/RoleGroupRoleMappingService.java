package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;

public interface RoleGroupRoleMappingService {

    // 🔎 LIST / SEARCH
    List<RoleGroupRoleMappingPojo> findAllMappings(Map<String, Object> params);

    List<RoleGroupRoleMappingPojo> findByRoleGroup(Map<String, Object> params); // params: roleGroupId

    List<RoleGroupRoleMappingPojo> findByRole(Map<String, Object> params); // params: roleId

    // ➕ CREATE
    RoleGroupRoleMappingPojo addRoleToGroup(Map<String, Object> params); // params: mapping

    // ➕ CREATE IF NOT EXISTS
    RoleGroupRoleMappingPojo addRoleToGroupIfNotExists(Map<String, Object> params); // params: roleGroupId, roleId

    // 🗑 DELETE
    void removeMapping(Map<String, Object> params); // params: id or roleGroupId+roleId
}
