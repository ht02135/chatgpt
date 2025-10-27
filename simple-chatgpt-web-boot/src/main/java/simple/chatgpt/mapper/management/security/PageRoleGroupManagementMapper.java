package simple.chatgpt.mapper.management.security;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;

@Mapper
public interface PageRoleGroupManagementMapper {

    // ======= 5 CORE METHODS (on top) =======
    void create(@Param("pageRoleGroup") PageRoleGroupManagementPojo pageRoleGroup);
    void update(@Param("id") Long id, @Param("pageRoleGroup") PageRoleGroupManagementPojo pageRoleGroup);
    List<PageRoleGroupManagementPojo> search(@Param("params") Map<String, Object> params);
    PageRoleGroupManagementPojo get(@Param("id") Long id);
    void delete(@Param("id") Long id);

    // ======= OTHER METHODS =======

}
