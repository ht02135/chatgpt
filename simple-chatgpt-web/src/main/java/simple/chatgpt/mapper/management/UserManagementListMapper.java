package simple.chatgpt.mapper.management;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import simple.chatgpt.pojo.management.UserManagementListPojo;

@Mapper
public interface UserManagementListMapper {

    List<UserManagementListPojo> searchUserLists(
            @Param("params") Map<String, Object> params,
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("sortField") String sortField,
            @Param("sortDirection") String sortDirection
    );
    
    int insertList(UserManagementListPojo list);
    int updateList(UserManagementListPojo list);
    int deleteList(Long id);
    UserManagementListPojo findListById(Long id);
    List<UserManagementListPojo> findAllLists();

    // Search + count
    List<UserManagementListPojo> findLists(@Param("params") Map<String, Object> params);
    long countLists(@Param("params") Map<String, Object> params);
}
