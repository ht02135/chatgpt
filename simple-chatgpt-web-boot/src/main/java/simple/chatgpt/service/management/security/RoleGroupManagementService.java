package simple.chatgpt.service.management.security;

import java.util.Map;

import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface RoleGroupManagementService {

    // 🔎 LIST / SEARCH
    PagedResult<RoleGroupManagementPojo> searchRoleGroups(Map<String, String> params);

    // 📖 READ
    RoleGroupManagementPojo getRoleGroupById(Long id);

    RoleGroupManagementPojo getByGroupName(String groupName);

    // ➕ CREATE
    RoleGroupManagementPojo createRoleGroup(RoleGroupManagementPojo group);

    // ✏️ UPDATE
    RoleGroupManagementPojo updateRoleGroupById(Long id, RoleGroupManagementPojo group);

    RoleGroupManagementPojo updateRoleGroupByName(String groupName, RoleGroupManagementPojo group);

    // 🗑 DELETE
    void deleteRoleGroupById(Long id);

    void deleteRoleGroupByName(String groupName);
}
