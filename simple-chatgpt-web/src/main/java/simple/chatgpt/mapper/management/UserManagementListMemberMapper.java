package simple.chatgpt.mapper.management;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;

@Mapper
public interface UserManagementListMemberMapper {

    // ➕ CREATE
    int insertMember(@Param("params") Map<String, Object> params);
    int batchInsertMembers(@Param("list") List<UserManagementListMemberPojo> members);

    // 🔎 SEARCH / LIST (Map-based params)
    List<UserManagementListMemberPojo> findMembers(@Param("params") Map<String, Object> params);
    long countMembers(@Param("params") Map<String, Object> params);

    // 📖 READ
    UserManagementListMemberPojo getMemberById(@Param("params") Map<String, Object> params);
    UserManagementListMemberPojo getMemberByUserName(@Param("params") Map<String, Object> params);

    // ✏️ UPDATE
    int updateMemberById(@Param("params") Map<String, Object> params);
    int updateMemberByUserName(@Param("params") Map<String, Object> params);

    // 🗑 DELETE
    int deleteMembersByListId(@Param("params") Map<String, Object> params);
    int deleteMemberById(@Param("params") Map<String, Object> params);
    int deleteMemberByUserName(@Param("params") Map<String, Object> params);

    // Legacy support
    List<UserManagementListMemberPojo> findMembersByListId(@Param("params") Map<String, Object> params);
}
