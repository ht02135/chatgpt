package simple.chatgpt.service.management;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.util.PagedResult;

/**
 * Service interface for managing user lists and their members.
 * Supports CRUD operations, CSV/Excel import/export, and member search/count.
 */
public interface UserManagementListService {

    /**
     * Search user lists with pagination, filtering, and sorting.
     *
     * @param params Map of search parameters (page, size, sortField, sortDirection, filters)
     * @return PagedResult of UserManagementListPojo
     */
    PagedResult<UserManagementListPojo> searchUserLists(Map<String, String> params);
    
    /**
     * Create a list with optional members.
     *
     * @param list    The list metadata
     * @param members Optional members to add to the list
     */
    void createList(UserManagementListPojo list, List<UserManagementListMemberPojo> members);

    /** Delete a list and all its members. */
    void deleteList(Long listId);

    /** Retrieve a list by its ID. */
    UserManagementListPojo getListById(Long listId);

    /** Retrieve all members of a given list. */
    List<UserManagementListMemberPojo> getMembersByListId(Long listId);

    /** Import members from a CSV file and attach to the list. */
    void importListFromCsv(InputStream inputStream, UserManagementListPojo list, String originalFileName) throws Exception;

    /** Export list members to a CSV file. */
    void exportListToCsv(Long listId, OutputStream outputStream) throws Exception;

    /** Import members from an Excel file (.xls or .xlsx) and attach to the list. */
    void importListFromExcel(InputStream inputStream, UserManagementListPojo list, String originalFileName) throws Exception;

    /** Export list members to an Excel file. */
    void exportListToExcel(Long listId, OutputStream outputStream) throws Exception;

    // ------------------ ADDED METHODS ------------------

    /** Search members using a map of parameters. */
    List<UserManagementListMemberPojo> searchMembers(Map<String, Object> params);

    /** Count members using a map of parameters. */
    long countMembers(Map<String, Object> params);
}
