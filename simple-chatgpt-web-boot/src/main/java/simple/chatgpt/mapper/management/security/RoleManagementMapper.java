package simple.chatgpt.mapper.management.security;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.RoleManagementPojo;

@Mapper
public interface RoleManagementMapper {

    // 🔹 Fetch all roles
    List<RoleManagementPojo> findAllRoles();

    // 🔹 Fetch role by ID
    RoleManagementPojo findRoleById(@Param("id") Long id);

    // 🔹 Fetch role by roleName
    RoleManagementPojo findRoleByName(@Param("roleName") String roleName);

    // 🔹 Search roles with parameters
    List<RoleManagementPojo> searchRoles(Map<String, String> params);

    // 🔹 Count roles with parameters
    int countRoles(Map<String, String> params);

    // 🔹 Insert a new role
    int insertRole(RoleManagementPojo role);

    // 🔹 Update an existing role
    int updateRole(RoleManagementPojo role);

    // 🔹 Delete role by ID
    int deleteRoleById(@Param("id") Long id);

    // 🔹 Delete role by roleName
    int deleteRoleByName(@Param("roleName") String roleName);
}
