package simple.chatgpt.mapper.management;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.UserManagementListPojo;

@Mapper
public interface UserManagementListMapper {

    // ---------------- CREATE ----------------
    int insertList(@Param("params") Map<String, Object> params);
    int createList(@Param("params") Map<String, Object> params);

    // ---------------- UPDATE ----------------
    int updateList(@Param("params") Map<String, Object> params);
    int updateListById(@Param("params") Map<String, Object> params);

    // ---------------- DELETE ----------------
    int deleteList(@Param("params") Map<String, Object> params);
    int deleteListById(@Param("params") Map<String, Object> params);

    // ---------------- READ ----------------
    UserManagementListPojo findListById(@Param("params") Map<String, Object> params);
    UserManagementListPojo getListById(@Param("params") Map<String, Object> params);

    List<UserManagementListPojo> findAllLists();
    List<UserManagementListPojo> getAllLists();

    // ---------------- SEARCH / PAGINATION ----------------
    List<UserManagementListPojo> findLists(@Param("params") Map<String, Object> params);
    List<UserManagementListPojo> searchLists(@Param("params") Map<String, Object> params);

    long countLists(@Param("params") Map<String, Object> params);

    // ---------------- LEGACY ----------------
    List<UserManagementListPojo> searchUserLists(@Param("params") Map<String, Object> params);
}
