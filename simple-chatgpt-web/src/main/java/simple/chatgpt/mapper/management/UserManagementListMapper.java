package simple.chatgpt.mapper.management;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import simple.chatgpt.pojo.management.UserManagementListPojo;

@Mapper
public interface UserManagementListMapper {

    // Existing CRUD methods
    int insertList(UserManagementListPojo list);
    int updateList(UserManagementListPojo list);
    int deleteList(Long id);
    UserManagementListPojo findListById(Long id);
    List<UserManagementListPojo> findAllLists();

    // 🔎 NEW METHODS for search + count
    List<UserManagementListPojo> findLists(Map<String, Object> params);
    long countLists(Map<String, Object> params);
}
