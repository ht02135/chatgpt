package simple.chatgpt.mapper.management.security;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;

@Mapper
public interface UserManagementRoleGroupMappingMapper {

    // ---------------- CREATE ----------------
    int insertUserRoleGroup(@Param("params") Map<String, Object> params);

    // ---------------- UPDATE ----------------
    int updateUserRoleGroup(@Param("params") Map<String, Object> params);

    // ---------------- DELETE ----------------
    int deleteUserRoleGroupById(@Param("params") Map<String, Object> params);
    int deleteUserRoleGroupByUserAndGroup(@Param("params") Map<String, Object> params);

    // ---------------- READ ----------------
    List<UserManagementRoleGroupMappingPojo> findAllUserRoleGroups();
    List<UserManagementRoleGroupMappingPojo> findByUserId(@Param("params") Map<String, Object> params);
    List<UserManagementRoleGroupMappingPojo> findByRoleGroupId(@Param("params") Map<String, Object> params);

    // ---------------- SEARCH / PAGINATION ----------------
    List<UserManagementRoleGroupMappingPojo> findUserRoleGroups(@Param("params") Map<String, Object> params);
    List<UserManagementRoleGroupMappingPojo> searchUserRoleGroups(@Param("params") Map<String, Object> params);
    long countUserRoleGroups(@Param("params") Map<String, Object> params);
}
