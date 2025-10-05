package simple.chatgpt.mapper.management.security;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.RoleManagementPojo;

@Mapper
public interface RoleManagementMapper {

    List<RoleManagementPojo> findAllRoles();

    RoleManagementPojo findRoleById(@Param("id") Long id);

    RoleManagementPojo findRoleByName(@Param("roleName") String roleName);

    List<RoleManagementPojo> searchRoles(Map<String, String> params);

    int countRoles(Map<String, String> params);

    int insertRole(RoleManagementPojo role);

    int updateRole(RoleManagementPojo role);

    int deleteRoleById(@Param("id") Long id);

    int deleteRoleByName(@Param("roleName") String roleName);
}
