package simple.chatgpt.mapper.management.security;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.security.PageManagementPojo;

@Mapper
public interface PageManagementMapper {

    // ======= 5 CORE METHODS (on top) =======
    void create(@Param("page") PageManagementPojo page);
    void update(@Param("id") Long id, @Param("page") PageManagementPojo page);
    List<PageManagementPojo> search(@Param("params") Map<String, Object> params);
    PageManagementPojo get(@Param("id") Long id);
    void delete(@Param("id") Long id);

    // ======= OTHER METHODS =======

}
