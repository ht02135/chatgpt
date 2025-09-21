package simple.chatgpt.mapper.management;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import simple.chatgpt.pojo.management.UserManagementListPojo;

@Mapper
public interface UserManagementListMapper {

    int insertList(UserManagementListPojo list);  // INSERT now includes originalFileName

    int updateList(UserManagementListPojo list);  // UPDATE now includes originalFileName

    int deleteList(Long id);

    UserManagementListPojo findListById(Long id);

    List<UserManagementListPojo> findAllLists();
}
