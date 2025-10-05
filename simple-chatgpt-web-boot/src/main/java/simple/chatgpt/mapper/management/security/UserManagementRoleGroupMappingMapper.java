package simple.chatgpt.mapper.management.security;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;

@Mapper
public interface UserManagementRoleGroupMappingMapper {

    List<UserManagementRoleGroupMappingPojo> findAllUserRoleGroups();

    List<UserManagementRoleGroupMappingPojo> findByUserId(@Param("userId") Long userId);

    List<UserManagementRoleGroupMappingPojo> findByRoleGroupId(@Param("roleGroupId") Long roleGroupId);

    int insertUserRoleGroup(UserManagementRoleGroupMappingPojo mapping);

    int deleteUserRoleGroupById(@Param("id") Long id);

    int deleteUserRoleGroupByUserAndGroup(@Param("userId") Long userId,
                                          @Param("roleGroupId") Long roleGroupId);
}
