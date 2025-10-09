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
    // ---------------- CREATE ----------------
    int insertRoleGroup(@Param("params") Map<String, Object> params); // matches <insert id="insertRoleGroup">

    // ---------------- UPDATE ----------------
    int updateRoleGroup(@Param("params") Map<String, Object> params); // matches <update id="updateRoleGroup">

    // ---------------- DELETE ----------------
    int deleteRoleGroupById(@Param("params") Map<String, Object> params);   // matches <delete id="deleteRoleGroupById">

    // ---------------- READ ----------------
    RoleGroupManagementPojo findRoleGroupById(@Param("params") Map<String, Object> params);   // matches <select id="findRoleGroupById">

    List<RoleGroupManagementPojo> findAllRoleGroups(); // matches <select id="findAllRoleGroups">
    List<RoleGroupManagementPojo> getAllRoleGroups();  // matches <select id="getAllRoleGroups">

    // ---------------- SEARCH / PAGINATION ----------------
    List<RoleGroupManagementPojo> findRoleGroups(@Param("params") Map<String, Object> params);   // matches <select id="findRoleGroups">
    List<RoleGroupManagementPojo> searchRoleGroups(@Param("params") Map<String, Object> params); // matches <select id="searchRoleGroups">

    // ---------------- COUNT ----------------
    long countRoleGroups(@Param("params") Map<String, Object> params); // matches <select id="countRoleGroups">
}
