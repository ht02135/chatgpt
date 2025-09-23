package simple.chatgpt.mapper.management;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;

@Mapper
public interface UserManagementListMemberMapper {

    // ➕ CREATE
    int insertMember(UserManagementListMemberPojo member);
    int batchInsertMembers(@Param("list") List<UserManagementListMemberPojo> members);

    // 🔎 SEARCH / LIST (Map-based params)
    List<UserManagementListMemberPojo> findMembers(Map<String, Object> params);
    long countMembers(Map<String, Object> params);

    // 📖 READ
    UserManagementListMemberPojo getMemberById(Long id);
    UserManagementListMemberPojo getMemberByUserName(String userName);

    // ✏️ UPDATE
    int updateMemberById(UserManagementListMemberPojo member);
    int updateMemberByUserName(UserManagementListMemberPojo member);

    // 🗑 DELETE
    int deleteMembersByListId(Long listId);
    int deleteMemberById(Long id);
    int deleteMemberByUserName(String userName);

    // Legacy support
    List<UserManagementListMemberPojo> findMembersByListId(Long listId);
}
