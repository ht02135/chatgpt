package simple.chatgpt.mapper.management.security;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;

@Mapper
public interface RoleGroupManagementMapper {

    // ---------------- CREATE ----------------
    int insertRoleGroup(@Param("params") Map<String, Object> params);

    // ---------------- UPDATE ----------------
    int updateRoleGroup(@Param("params") Map<String, Object> params);

    // ---------------- DELETE ----------------
    int deleteRoleGroupById(@Param("params") Map<String, Object> params);
    int deleteRoleGroupByName(@Param("params") Map<String, Object> params);

    // ---------------- READ ----------------
    RoleGroupManagementPojo findRoleGroupById(@Param("params") Map<String, Object> params);
    RoleGroupManagementPojo findRoleGroupByName(@Param("params") Map<String, Object> params);

    List<RoleGroupManagementPojo> findAllRoleGroups();
    List<RoleGroupManagementPojo> getAllRoleGroups();

    // ---------------- SEARCH / PAGINATION ----------------
    List<RoleGroupManagementPojo> findRoleGroups(@Param("params") Map<String, Object> params);
    List<RoleGroupManagementPojo> searchRoleGroups(@Param("params") Map<String, Object> params);

    long countRoleGroups(@Param("params") Map<String, Object> params);
}
