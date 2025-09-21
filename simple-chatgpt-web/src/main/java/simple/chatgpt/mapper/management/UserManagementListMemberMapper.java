package simple.chatgpt.mapper.management;

// UserManagementListMemberMapper.java
import java.util.List;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;

public interface UserManagementListMemberMapper {
    int insertMember(UserManagementListMemberPojo member);
    int batchInsertMembers(List<UserManagementListMemberPojo> members);
    int deleteMembersByListId(Long listId);
    List<UserManagementListMemberPojo> findMembersByListId(Long listId);
}
