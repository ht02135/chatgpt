package simple.chatgpt.service.management.security;

import java.util.Map;

import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.util.PagedResult;

public interface RoleGroupManagementService {

    // ---------------- CREATE ----------------
	RoleGroupManagementPojo insertRoleGroup(Map<String, Object> params);

    // ---------------- UPDATE ----------------
	RoleGroupManagementPojo updateRoleGroup(Map<String, Object> params);

    // ---------------- DELETE ----------------
    void deleteRoleGroupById(Map<String, Object> params);
    void deleteRoleGroupByName(Map<String, Object> params);

    // ---------------- READ ----------------
    RoleGroupManagementPojo findRoleGroupById(Map<String, Object> params);
    RoleGroupManagementPojo findRoleGroupByName(Map<String, Object> params);

    PagedResult<RoleGroupManagementPojo> findAllRoleGroups();
    PagedResult<RoleGroupManagementPojo> getAllRoleGroups();

    // ---------------- SEARCH / PAGINATION ----------------
    PagedResult<RoleGroupManagementPojo> findRoleGroups(Map<String, Object> params);
    PagedResult<RoleGroupManagementPojo> searchRoleGroups(Map<String, Object> params);

    // ---------------- COUNT ----------------
    long countRoleGroups(Map<String, Object> params);
}
