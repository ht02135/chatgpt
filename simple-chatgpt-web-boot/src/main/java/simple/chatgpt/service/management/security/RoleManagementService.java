package simple.chatgpt.service.management.security;

import java.util.Map;
import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface RoleManagementService {

    // ------------------ SEARCH / PAGINATION ------------------
    PagedResult<RoleManagementPojo> searchRoles(Map<String, Object> params);

    // ------------------ READ ------------------
    RoleManagementPojo getRole(Map<String, Object> params); // params should include "roleId" or "roleName"

    // ------------------ CREATE ------------------
    RoleManagementPojo createRole(Map<String, Object> params); // params should include "role"

    // ------------------ UPDATE ------------------
    RoleManagementPojo updateRole(Map<String, Object> params); // params should include "roleId" or "roleName", and "role"

    // ------------------ DELETE ------------------
    void deleteRole(Map<String, Object> params); // params should include "roleId" or "roleName"
}
