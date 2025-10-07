package simple.chatgpt.controller.management;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

public interface UserManagementListControllerApi {

    // ------------------ LIST SEARCH ------------------
    ResponseEntity<Response<PagedResult<UserManagementListPojo>>> searchUserLists(
            @RequestParam Map<String, Object> params
    );

    // ------------------ CREATE LIST WITH MEMBERS ------------------
    ResponseEntity<Response<UserManagementListPojo>> createList(
            UserManagementListPojo list,
            UserManagementListMemberPojo[] members
    );

    // ------------------ GET LIST BY ID ------------------
    ResponseEntity<Response<UserManagementListPojo>> getListById(Long listId);

    // ------------------ GET MEMBERS BY LIST ------------------
    ResponseEntity<Response<PagedResult<UserManagementListMemberPojo>>> getMembersByListId(Long listId);

    // ------------------ DELETE LIST ------------------
    ResponseEntity<Response<Void>> deleteList(Long listId);

    // ------------------ IMPORT LIST ------------------
    ResponseEntity<Response<UserManagementListPojo>> importList(
            UserManagementListPojo list,
            MultipartFile file
    );

    // ------------------ EXPORT LIST ------------------
    void exportListToCsv(Long listId, HttpServletResponse response);
    void exportListToExcel(Long listId, HttpServletResponse response);

    // ------------------ DOWNLOAD SAMPLE CSV ------------------
    void downloadSampleCsv(HttpServletRequest request, HttpServletResponse response);

    // ------------------ SEARCH MEMBERS ------------------
    ResponseEntity<Response<PagedResult<UserManagementListMemberPojo>>> searchMembers(
            @RequestParam Map<String, Object> params
    );

    ResponseEntity<Response<Long>> countMembers(@RequestParam Map<String, Object> params);
}
