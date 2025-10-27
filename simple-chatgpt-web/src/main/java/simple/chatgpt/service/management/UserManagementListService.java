package simple.chatgpt.service.management;

import java.util.Map;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.util.PagedResult;

public interface UserManagementListService {

    // ------------------ LIST SEARCH ------------------
    PagedResult<UserManagementListPojo> searchUserLists(Map<String, Object> params);

    // ------------------ LIST CRUD ------------------
    void createList(Map<String, Object> params); // params should include "list" and "members"
    void deleteList(Map<String, Object> params); // params should include "listId"
    void updateList(Map<String, Object> params);
    UserManagementListPojo getListById(Map<String, Object> params); // params should include "listId"

    // ------------------ MEMBER CRUD ------------------
    PagedResult<UserManagementListMemberPojo> getMembersByListId(Map<String, Object> params); // params should include "listId"
    PagedResult<UserManagementListMemberPojo> searchMembers(Map<String, Object> params);
    long countMembers(Map<String, Object> params);

    // ------------------ FILE IMPORT/EXPORT ------------------
    void importListFromCsv(Map<String, Object> params) throws Exception;   // params include InputStream, list, originalFileName
    void exportListToCsv(Map<String, Object> params) throws Exception;     // params include listId, OutputStream
    void importListFromExcel(Map<String, Object> params) throws Exception; // params include InputStream, list, originalFileName
    void exportListToExcel(Map<String, Object> params) throws Exception;   // params include listId, OutputStream
}
