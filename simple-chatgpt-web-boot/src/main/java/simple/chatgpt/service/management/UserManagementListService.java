package simple.chatgpt.service.management;

import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.util.PagedResult;

public interface UserManagementListService {

	// ======= 5 CORE METHODS (on top) =======
	UserManagementListPojo create(UserManagementListPojo list);
	UserManagementListPojo update(Long id, UserManagementListPojo list);
	PagedResult<UserManagementListPojo> search(Map<String, String> params);
	UserManagementListPojo get(Long id);
	void delete(Long id);

	// ======= OTHER METHODS =======

    // ------------------ FILE IMPORT/EXPORT ------------------
    void importListFromCsv(Map<String, Object> params) throws Exception;   // params include InputStream, list, originalFileName
    void exportListToCsv(Map<String, Object> params) throws Exception;     // params include listId, OutputStream
    void importListFromExcel(Map<String, Object> params) throws Exception; // params include InputStream, list, originalFileName
    void exportListToExcel(Map<String, Object> params) throws Exception;   // params include listId, OutputStream
    
    List<UserManagementListPojo> getAll();
}
