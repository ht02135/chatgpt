package simple.chatgpt.mapper.management.security;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.RoleManagementPojo;

@Mapper
public interface RoleManagementMapper {

    // ---------------- CREATE ----------------
    int insertRole(@Param("params") Map<String, Object> params);

    // ---------------- UPDATE ----------------
    int updateRole(@Param("params") Map<String, Object> params);

    // ---------------- DELETE ----------------
    int deleteRoleById(@Param("params") Map<String, Object> params);
    int deleteRoleByName(@Param("params") Map<String, Object> params);

    // ---------------- READ ----------------
    RoleManagementPojo findRoleById(@Param("params") Map<String, Object> params);
    RoleManagementPojo findRoleByName(@Param("params") Map<String, Object> params);

    List<RoleManagementPojo> findAllRoles();
    List<RoleManagementPojo> getAllRoles();

    // ---------------- SEARCH / PAGINATION ----------------
    List<RoleManagementPojo> findRoles(@Param("params") Map<String, Object> params);
    List<RoleManagementPojo> searchRoles(@Param("params") Map<String, Object> params);

    long countRoles(@Param("params") Map<String, Object> params);
}
