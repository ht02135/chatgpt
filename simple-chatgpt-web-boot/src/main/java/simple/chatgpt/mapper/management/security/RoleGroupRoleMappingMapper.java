package simple.chatgpt.mapper.management.security;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;

@Mapper
public interface RoleGroupRoleMappingMapper {

    List<RoleGroupRoleMappingPojo> findAllMappings();

    List<RoleGroupRoleMappingPojo> findByRoleGroupId(@Param("roleGroupId") Long roleGroupId);

    List<RoleGroupRoleMappingPojo> findByRoleId(@Param("roleId") Long roleId);

    int insertMapping(RoleGroupRoleMappingPojo mapping);

    int deleteMappingById(@Param("id") Long id);

    int deleteMappingByGroupAndRole(@Param("roleGroupId") Long roleGroupId,
                                    @Param("roleId") Long roleId);
}
