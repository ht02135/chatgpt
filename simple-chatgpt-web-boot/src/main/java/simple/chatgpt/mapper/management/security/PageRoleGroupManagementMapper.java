package simple.chatgpt.mapper.management.security;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.PageRoleGroupManagementPojo;

@Mapper
public interface PageRoleGroupManagementMapper {

    // ---------------- CREATE ----------------
    int insertPageRoleGroup(@Param("params") Map<String, Object> params);

    // ---------------- UPDATE ----------------
    int updatePageRoleGroup(@Param("params") Map<String, Object> params);

    // ---------------- DELETE ----------------
    int deletePageRoleGroupById(@Param("params") Map<String, Object> params);

    // ---------------- READ ----------------
    List<PageRoleGroupManagementPojo> findAllPageRoleGroups();
    PageRoleGroupManagementPojo findById(@Param("params") Map<String, Object> params);
    PageRoleGroupManagementPojo findByUrlPattern(@Param("params") Map<String, Object> params);
    List<PageRoleGroupManagementPojo> findByRoleGroupId(@Param("params") Map<String, Object> params);

    // ---------------- SEARCH / PAGINATION ----------------
    List<PageRoleGroupManagementPojo> findPageRoleGroups(@Param("params") Map<String, Object> params);
    List<PageRoleGroupManagementPojo> searchPageRoleGroups(@Param("params") Map<String, Object> params);
    long countPageRoleGroups(@Param("params") Map<String, Object> params);
}
