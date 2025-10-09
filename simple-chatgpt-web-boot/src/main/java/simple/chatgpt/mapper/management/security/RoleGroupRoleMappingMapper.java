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
    // ---------------- CREATE ----------------
    int insertMapping(@Param("params") Map<String, Object> params);

    // ---------------- DELETE ----------------
    int deleteMappingById(@Param("params") Map<String, Object> params);
    int deleteMappingByGroupAndRole(@Param("params") Map<String, Object> params);

    // ---------------- READ ----------------
    RoleGroupRoleMappingPojo findById(@Param("params") Map<String, Object> params);
    List<RoleGroupRoleMappingPojo> findAllMappings();
    List<RoleGroupRoleMappingPojo> findByRoleGroupId(@Param("params") Map<String, Object> params);
    List<RoleGroupRoleMappingPojo> findByRoleId(@Param("params") Map<String, Object> params);

    // ---------------- SEARCH / PAGINATION ----------------
    List<RoleGroupRoleMappingPojo> findMappings(@Param("params") Map<String, Object> params);
    List<RoleGroupRoleMappingPojo> searchMappings(@Param("params") Map<String, Object> params);
    long countMappings(@Param("params") Map<String, Object> params);
}
