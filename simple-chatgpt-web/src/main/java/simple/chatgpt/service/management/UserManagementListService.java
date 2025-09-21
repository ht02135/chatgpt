package simple.chatgpt.service.management;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;

public interface UserManagementListService {

    // Core CRUD
    void createList(UserManagementListPojo list, List<UserManagementListMemberPojo> members);
    void deleteList(Long listId);
    UserManagementListPojo getListById(Long listId);
    List<UserManagementListMemberPojo> getMembersByListId(Long listId);

    // CSV Import/Export
    void importListFromCsv(InputStream inputStream, UserManagementListPojo list, String originalFileName) throws Exception;
    void exportListToCsv(Long listId, OutputStream outputStream) throws Exception;

    // Excel Import/Export
    void importListFromExcel(InputStream inputStream, UserManagementListPojo list, String originalFileName) throws Exception;
    void exportListToExcel(Long listId, OutputStream outputStream) throws Exception;
}
