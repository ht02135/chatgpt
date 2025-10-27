package simple.chatgpt.mapper.management.security;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.RoleManagementPojo;

@Mapper
public interface RoleManagementMapper {

    // ======= 5 CORE METHODS (on top) =======
    void create(@Param("role") RoleManagementPojo role);
    void update(@Param("id") Long id, @Param("role") RoleManagementPojo role);
    List<RoleManagementPojo> search(@Param("params") Map<String, Object> params);
    RoleManagementPojo get(@Param("id") Long id);
    void delete(@Param("id") Long id);

    // ======= OTHER METHODS =======

}
