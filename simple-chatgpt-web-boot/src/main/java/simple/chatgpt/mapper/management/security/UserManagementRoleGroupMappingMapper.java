package simple.chatgpt.mapper.management.security;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;

@Mapper
public interface UserManagementRoleGroupMappingMapper {

    // ======= 5 CORE METHODS (on top) =======
    void create(@Param("mapping") UserManagementRoleGroupMappingPojo mapping);
    void update(@Param("id") Long id, @Param("mapping") UserManagementRoleGroupMappingPojo mapping);
    List<UserManagementRoleGroupMappingPojo> search(@Param("params") Map<String, Object> params);
    UserManagementRoleGroupMappingPojo get(@Param("id") Long id);
    void delete(@Param("id") Long id);

    // ======= OTHER METHODS =======
    
}
