package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.util.PagedResult;

public interface UserManagementRoleGroupMappingService {

    // ======= 5 CORE METHODS (on top) =======
    UserManagementRoleGroupMappingPojo create(UserManagementRoleGroupMappingPojo mapping);
    UserManagementRoleGroupMappingPojo update(Long id, UserManagementRoleGroupMappingPojo mapping);
    PagedResult<UserManagementRoleGroupMappingPojo> search(Map<String, String> params);
    UserManagementRoleGroupMappingPojo get(Long id);
    void delete(Long id);

    // ======= OTHER METHODS =======
    List<UserManagementRoleGroupMappingPojo> getMappingsByParams(Map<String, Object> params);
    List<UserManagementRoleGroupMappingPojo> getMappingsByUserId(Long userId); // #{params.userId}
    List<UserManagementRoleGroupMappingPojo> getAll();

    // ======= HELPER METHODS FOR CONTROLLER =======
    void syncUserRoleGroups(Long userId, List<RoleGroupManagementPojo> newRoleGroups);
    List<RoleGroupManagementPojo> getUserRoleGroups(Long userId);
    void deleteMappingsByUserId(Long userId);
}
