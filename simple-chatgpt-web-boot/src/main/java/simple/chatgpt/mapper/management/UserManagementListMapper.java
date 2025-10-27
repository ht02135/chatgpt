package simple.chatgpt.mapper.management;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.UserManagementListPojo;

public interface UserManagementListMapper {

    // ======= 5 CORE METHODS (on top) =======
    void create(@Param("list") UserManagementListPojo list);
    void update(@Param("id") Long id, @Param("list") UserManagementListPojo list);
    List<UserManagementListPojo> search(@Param("params") Map<String, Object> params);
    UserManagementListPojo get(@Param("id") Long id);
    void delete(@Param("id") Long id);

    // ======= OTHER METHODS =======

}
