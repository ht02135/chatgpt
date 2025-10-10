package simple.chatgpt.mapper.management.security;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;

@Mapper
public interface RoleGroupRoleMappingMapper {

    // ======= 5 CORE METHODS (on top) =======
    void create(@Param("mapping") RoleGroupRoleMappingPojo mapping);
    void update(@Param("id") Long id, @Param("mapping") RoleGroupRoleMappingPojo mapping);
    List<RoleGroupRoleMappingPojo> search(@Param("params") Map<String, Object> params);
    RoleGroupRoleMappingPojo get(@Param("id") Long id);
    void delete(@Param("id") Long id);

    // ======= OTHER METHODS =======

}
