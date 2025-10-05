package simple.chatgpt.service.management;

import java.util.Map;

import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface RoleManagementService {

    // 🔎 LIST / SEARCH
    PagedResult<RoleManagementPojo> searchRoles(Map<String, String> params);

    // 📖 READ
    RoleManagementPojo getRoleById(Long id);

    RoleManagementPojo getByRoleName(String roleName);

    // ➕ CREATE
    RoleManagementPojo createRole(RoleManagementPojo role);

    // ✏️ UPDATE
    RoleManagementPojo updateRoleById(Long id, RoleManagementPojo role);

    RoleManagementPojo updateRoleByRoleName(String roleName, RoleManagementPojo role);

    // 🗑 DELETE
    void deleteRoleById(Long id);

    void deleteRoleByRoleName(String roleName);
}
