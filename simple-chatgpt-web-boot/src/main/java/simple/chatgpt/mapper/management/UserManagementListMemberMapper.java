package simple.chatgpt.mapper.management;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;

public interface UserManagementListMemberMapper {

    // ======= 5 CORE METHODS (on top) =======
    void create(@Param("member") UserManagementListMemberPojo member);
    void update(@Param("id") Long id, @Param("member") UserManagementListMemberPojo member);
    List<UserManagementListMemberPojo> search(@Param("params") Map<String, Object> params);
    UserManagementListMemberPojo get(@Param("id") Long id);
    void delete(@Param("id") Long id);

    // ======= OTHER METHODS =======

    List<UserManagementListMemberPojo> findMembersByListId(@Param("params") Map<String, Object> params);
}
