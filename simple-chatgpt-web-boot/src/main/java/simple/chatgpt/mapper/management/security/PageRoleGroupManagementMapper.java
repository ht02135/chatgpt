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

    // ---------------- COUNT ----------------
    long countPageRoleGroups(@Param("params") Map<String, Object> params);
}
