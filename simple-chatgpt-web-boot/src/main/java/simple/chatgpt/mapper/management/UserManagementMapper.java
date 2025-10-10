package simple.chatgpt.mapper.management;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.UserManagementPojo;

public interface UserManagementMapper {

    // ======= 5 CORE METHODS (on top) =======
    void create(@Param("user") UserManagementPojo user);
    void update(@Param("id") Long id, @Param("user") UserManagementPojo user);
    List<UserManagementPojo> search(@Param("params") Map<String, Object> params);
    UserManagementPojo get(@Param("id") Long id);
    void delete(@Param("id") Long id);

    // ======= OTHER METHODS  =======

}
