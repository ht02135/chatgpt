package simple.chatgpt.mapper.management;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;

@Mapper
public interface UserManagementListMemberMapper {
    int insertMember(UserManagementListMemberPojo member);
    int batchInsertMembers(@Param("list") List<UserManagementListMemberPojo> members);
    int deleteMembersByListId(Long listId);
    List<UserManagementListMemberPojo> findMembersByListId(Long listId);
}
