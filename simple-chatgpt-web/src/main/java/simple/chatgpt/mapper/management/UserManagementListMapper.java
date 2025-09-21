package simple.chatgpt.mapper.management;

import java.util.List;

import simple.chatgpt.pojo.management.UserManagementListPojo;

public interface UserManagementListMapper {
    int insertList(UserManagementListPojo list);
    int updateList(UserManagementListPojo list);
    int deleteList(Long id);
    UserManagementListPojo findListById(Long id);
    List<UserManagementListPojo> findAllLists();
}
