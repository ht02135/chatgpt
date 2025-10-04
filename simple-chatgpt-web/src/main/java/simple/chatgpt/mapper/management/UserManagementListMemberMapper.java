package simple.chatgpt.mapper.management;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;

@Mapper
public interface UserManagementListMemberMapper {

    // ---------------- CREATE ----------------
    int insertMember(@Param("params") Map<String, Object> params);
    int createMember(@Param("params") Map<String, Object> params); // service uses createMember
    int batchInsertMembers(@Param("params") Map<String, Object> params); // service uses batchCreateMembers

    // ---------------- READ ----------------
    UserManagementListMemberPojo getMemberById(@Param("params") Map<String, Object> params);
    UserManagementListMemberPojo getMemberByUserName(@Param("params") Map<String, Object> params);

    // ---------------- UPDATE ----------------
    int updateMemberById(@Param("params") Map<String, Object> params);
    int updateMemberByUserName(@Param("params") Map<String, Object> params);

    // ---------------- DELETE ----------------
    int deleteMemberById(@Param("params") Map<String, Object> params);
    int deleteMemberByUserName(@Param("params") Map<String, Object> params);
    int deleteMembersByListId(@Param("params") Map<String, Object> params);

    // ---------------- SEARCH / LIST ----------------
    List<UserManagementListMemberPojo> findMembers(@Param("params") Map<String, Object> params);
    List<UserManagementListMemberPojo> searchMembers(@Param("params") Map<String, Object> params); // service uses searchMembers
    List<UserManagementListMemberPojo> findMembersByListId(@Param("params") Map<String, Object> params);
    long countMembers(@Param("params") Map<String, Object> params);
}
