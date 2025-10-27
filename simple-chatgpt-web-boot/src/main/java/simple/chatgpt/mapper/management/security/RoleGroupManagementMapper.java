package simple.chatgpt.mapper.management.security;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;

@Mapper
public interface RoleGroupManagementMapper {

    // ======= 5 CORE METHODS (on top) =======
    void create(@Param("roleGroup") RoleGroupManagementPojo roleGroup);
    void update(@Param("id") Long id, @Param("roleGroup") RoleGroupManagementPojo roleGroup);
    List<RoleGroupManagementPojo> search(@Param("params") Map<String, Object> params);
    RoleGroupManagementPojo get(@Param("id") Long id);
    void delete(@Param("id") Long id);

    // ======= OTHER METHODS =======

}
