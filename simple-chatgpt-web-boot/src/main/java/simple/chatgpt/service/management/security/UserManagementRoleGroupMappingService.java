package simple.chatgpt.service.management.security;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;

public interface UserManagementRoleGroupMappingService {

    List<UserManagementRoleGroupMappingPojo> findAll();

    List<UserManagementRoleGroupMappingPojo> findByUserId(Map<String, Object> params);

    List<UserManagementRoleGroupMappingPojo> findByRoleGroupId(Map<String, Object> params);

    UserManagementRoleGroupMappingPojo addUserToRoleGroup(Map<String, Object> params);

    void removeMappingById(Map<String, Object> params);

    void removeMappingByUserAndGroup(Map<String, Object> params);
}
