package simple.chatgpt.service.management.security;

import java.util.Map;

import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface RoleGroupManagementService {

    // 🔎 SEARCH / PAGINATION
    PagedResult<RoleGroupManagementPojo> searchRoleGroups(Map<String, Object> params);

    // 📖 READ
    RoleGroupManagementPojo getRoleGroup(Map<String, Object> params); // params: roleGroupId or groupName

    // ➕ CREATE
    RoleGroupManagementPojo createRoleGroup(Map<String, Object> params); // params: group

    // ✏️ UPDATE
    RoleGroupManagementPojo updateRoleGroup(Map<String, Object> params); // params: roleGroupId or groupName, group

    // 🗑 DELETE
    void deleteRoleGroup(Map<String, Object> params); // params: roleGroupId or groupName
}
