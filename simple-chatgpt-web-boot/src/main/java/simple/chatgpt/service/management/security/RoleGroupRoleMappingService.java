package simple.chatgpt.service.management.security;

import java.util.List;

import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;

public interface RoleGroupRoleMappingService {

    // 🔎 LIST / SEARCH
    List<RoleGroupRoleMappingPojo> findAllMappings();

    List<RoleGroupRoleMappingPojo> findByRoleGroupId(Long roleGroupId);

    List<RoleGroupRoleMappingPojo> findByRoleId(Long roleId);

    // ➕ CREATE
    RoleGroupRoleMappingPojo addRoleToGroup(RoleGroupRoleMappingPojo mapping);

    // 🗑 DELETE
    void removeMappingById(Long id);

    void removeMappingByGroupAndRole(Long roleGroupId, Long roleId);
}
