package simple.chatgpt.mapper.management.security;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;

@Mapper
public interface PageRoleGroupManagementMapper {

    List<PageRoleGroupManagementPojo> findAllPageRoleGroups();

    PageRoleGroupManagementPojo findByUrlPattern(@Param("urlPattern") String urlPattern);

    List<PageRoleGroupManagementPojo> findByRoleGroupId(@Param("roleGroupId") Long roleGroupId);

    int insertPageRoleGroup(PageRoleGroupManagementPojo pageRoleGroup);

    int updatePageRoleGroup(PageRoleGroupManagementPojo pageRoleGroup);

    int deletePageRoleGroupById(@Param("id") Long id);
}
