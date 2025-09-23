package simple.chatgpt.service.management;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.util.PagedResult;

public interface UserManagementListService {

    // ------------------ LIST SEARCH ------------------
    PagedResult<UserManagementListPojo> searchUserLists(Map<String, String> params);

    // ------------------ LIST CRUD ------------------
    void createList(UserManagementListPojo list, List<UserManagementListMemberPojo> members);
    void deleteList(Long listId);
    UserManagementListPojo getListById(Long listId);

    // ------------------ MEMBER CRUD ------------------
    List<UserManagementListMemberPojo> getMembersByListId(Long listId);
    List<UserManagementListMemberPojo> searchMembers(Map<String, Object> params);
    long countMembers(Map<String, Object> params);

    // ------------------ FILE IMPORT/EXPORT ------------------
    void importListFromCsv(InputStream inputStream, UserManagementListPojo list, String originalFileName) throws Exception;
    void exportListToCsv(Long listId, OutputStream outputStream) throws Exception;
    void importListFromExcel(InputStream inputStream, UserManagementListPojo list, String originalFileName) throws Exception;
    void exportListToExcel(Long listId, OutputStream outputStream) throws Exception;
}
