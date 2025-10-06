package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;

public interface RoleGroupManagementService {

    // ---------------- CREATE ----------------
    int insertRoleGroup(Map<String, Object> params);

    // ---------------- UPDATE ----------------
    int updateRoleGroup(Map<String, Object> params);

    // ---------------- DELETE ----------------
    int deleteRoleGroupById(Map<String, Object> params);
    int deleteRoleGroupByName(Map<String, Object> params);

    // ---------------- READ ----------------
    RoleGroupManagementPojo findRoleGroupById(Map<String, Object> params);
    RoleGroupManagementPojo findRoleGroupByName(Map<String, Object> params);

    List<RoleGroupManagementPojo> findAllRoleGroups();
    List<RoleGroupManagementPojo> getAllRoleGroups();

    // ---------------- SEARCH / PAGINATION ----------------
    List<RoleGroupManagementPojo> findRoleGroups(Map<String, Object> params);
    List<RoleGroupManagementPojo> searchRoleGroups(Map<String, Object> params);

    // ---------------- COUNT ----------------
    long countRoleGroups(Map<String, Object> params);
}
