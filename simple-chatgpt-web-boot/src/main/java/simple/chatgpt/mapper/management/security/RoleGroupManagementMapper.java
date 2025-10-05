package simple.chatgpt.mapper.management.security;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;

@Mapper
public interface RoleGroupManagementMapper {

    List<RoleGroupManagementPojo> findAllRoleGroups();

    RoleGroupManagementPojo findRoleGroupById(@Param("id") Long id);

    RoleGroupManagementPojo findRoleGroupByName(@Param("groupName") String groupName);

    int insertRoleGroup(RoleGroupManagementPojo group);

    int updateRoleGroup(RoleGroupManagementPojo group);

    int deleteRoleGroupById(@Param("id") Long id);

    int deleteRoleGroupByName(@Param("groupName") String groupName);
}
