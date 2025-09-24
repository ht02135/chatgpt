package simple.chatgpt.controller.management;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import simple.chatgpt.pojo.management.UserManagementListMemberPojo;
import simple.chatgpt.pojo.management.UserManagementListPojo;
import simple.chatgpt.service.management.UserManagementListService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/userlists", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementListController {

    private static final Logger logger = LogManager.getLogger(UserManagementListController.class);

    private final UserManagementListService userManagementListService;

    public UserManagementListController(UserManagementListService userManagementListService) {
        this.userManagementListService = userManagementListService;
    }

    // ------------------ LIST SEARCH ------------------
    @GetMapping
    public ResponseEntity<Response<PagedResult<UserManagementListPojo>>> searchUserLists(
            @RequestParam Map<String, Object> params
    ) {
        logger.debug("searchUserLists called with params={}", params);

        // Default pagination and sorting
        int page = Integer.parseInt(params.getOrDefault("page", "0").toString());
        int size = Integer.parseInt(params.getOrDefault("size", "20").toString());
        int offset = page * size;

        params.put("offset", offset);
        params.put("limit", size);
        params.put("sortField", params.getOrDefault("sortField", "id"));
        params.put("sortDirection", params.getOrDefault("sortDirection", "asc"));

        PagedResult<UserManagementListPojo> lists = userManagementListService.searchUserLists(params);

        return ResponseEntity.ok(Response.success("Fetched successfully", lists, HttpStatus.OK.value()));
    }

    // ------------------ CREATE LIST WITH MEMBERS ------------------
    @PostMapping("/create")
    public ResponseEntity<Response<UserManagementListPojo>> createList(
            @RequestPart("list") UserManagementListPojo list,
            @RequestPart(value = "members", required = false) UserManagementListMemberPojo[] members
    ) {
        logger.debug("createList #############");
        logger.debug("createList list={}", list);
        logger.debug("createList #############");
        if (list != null) {
            logger.debug("createList list.userListName={}", list.getUserListName());
            logger.debug("createList list.description={}", list.getDescription());
        }

        if (members != null) {
            for (UserManagementListMemberPojo m : members) {
                logger.debug("createList member={}", m);
                logger.debug("createList member.userName={}", m.getUserName());
                logger.debug("createList member.firstName={}", m.getFirstName());
                logger.debug("createList member.lastName={}", m.getLastName());
                logger.debug("createList member.email={}", m.getEmail());
            }
        } else {
            logger.debug("createList members=null");
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("list", list);
            params.put("members", members != null ? Arrays.asList(members) : null);
            userManagementListService.createList(params);
        } catch (Exception e) {
            logger.error("createList failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Create failed: " + e.getMessage(), null, 500));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("List created successfully", list, HttpStatus.CREATED.value()));
    }

    // ------------------ GET LIST BY ID ------------------
    @GetMapping("/get")
    public ResponseEntity<Response<UserManagementListPojo>> getList(@RequestParam Long listId) {
        logger.debug("getList #############");
        logger.debug("getList listId={}", listId);
        logger.debug("getList #############");

        Map<String, Object> params = new HashMap<>();
        params.put("listId", listId);

        UserManagementListPojo list = userManagementListService.getListById(params);
        if (list == null) {
            return ResponseEntity.ok(Response.error("List not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("List fetched successfully", list, HttpStatus.OK.value()));
    }

    // ------------------ GET MEMBERS BY LIST ------------------
    @GetMapping("/members")
    public ResponseEntity<Response<List<UserManagementListMemberPojo>>> getMembers(@RequestParam Long listId) {
        logger.debug("getMembers #############");
        logger.debug("getMembers listId={}", listId);
        logger.debug("getMembers #############");

        Map<String, Object> params = new HashMap<>();
        params.put("listId", listId);

        List<UserManagementListMemberPojo> members = userManagementListService.getMembersByListId(params);
        return ResponseEntity.ok(Response.success("Members fetched successfully", members, HttpStatus.OK.value()));
    }

    // ------------------ DELETE LIST ------------------
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteList(@RequestParam Long listId) {
        logger.debug("deleteList #############");
        logger.debug("deleteList listId={}", listId);
        logger.debug("deleteList #############");

        Map<String, Object> params = new HashMap<>();
        params.put("listId", listId);

        userManagementListService.deleteList(params);
        return ResponseEntity.ok(Response.success("List deleted successfully", null, HttpStatus.OK.value()));
    }

    // ------------------ IMPORT LIST ------------------
    @PostMapping("/import")
    public ResponseEntity<Response<UserManagementListPojo>> importList(
            @RequestPart("list") UserManagementListPojo list,
            @RequestPart("file") MultipartFile file
    ) {
        logger.debug("importList #############");
        logger.debug("importList list={}", list);
        logger.debug("importList fileName={}", file.getOriginalFilename());
        logger.debug("importList #############");

        try (var is = file.getInputStream()) {
            String filename = file.getOriginalFilename().toLowerCase();
            Map<String, Object> params = new HashMap<>();
            params.put("list", list);
            params.put("inputStream", is);
            params.put("originalFileName", file.getOriginalFilename());

            if (filename.endsWith(".csv")) {
                userManagementListService.importListFromCsv(params);
            } else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
                userManagementListService.importListFromExcel(params);
            } else {
                return ResponseEntity.ok(Response.error("Unsupported file type", null, HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            logger.error("importList failed", e);
            return ResponseEntity.ok(Response.error("Import failed: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("List imported successfully", list, HttpStatus.CREATED.value()));
    }

    // ------------------ EXPORT LIST ------------------
    @GetMapping("/export/csv")
    public void exportListToCsv(@RequestParam Long listId, HttpServletResponse response) {
        logger.debug("exportListToCsv #############");
        logger.debug("exportListToCsv listId={}", listId);
        logger.debug("exportListToCsv #############");

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"list_" + listId + ".csv\"");

        try (var os = response.getOutputStream()) {
            Map<String, Object> params = new HashMap<>();
            params.put("listId", listId);
            params.put("outputStream", os);
            userManagementListService.exportListToCsv(params);
        } catch (Exception e) {
            logger.error("exportListToCsv failed", e);
        }
    }

    @GetMapping("/export/excel")
    public void exportListToExcel(@RequestParam Long listId, HttpServletResponse response) {
        logger.debug("exportListToExcel #############");
        logger.debug("exportListToExcel listId={}", listId);
        logger.debug("exportListToExcel #############");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"list_" + listId + ".xlsx\"");

        try (var os = response.getOutputStream()) {
            Map<String, Object> params = new HashMap<>();
            params.put("listId", listId);
            params.put("outputStream", os);
            userManagementListService.exportListToExcel(params);
        } catch (Exception e) {
            logger.error("exportListToExcel failed", e);
        }
    }

    // ------------------ SEARCH MEMBERS ------------------
    @GetMapping("/members/search")
    public ResponseEntity<Response<List<UserManagementListMemberPojo>>> searchMembers(
            @RequestParam Map<String, Object> params
    ) {
        logger.debug("searchMembers #############");
        logger.debug("searchMembers params={}", params);
        logger.debug("searchMembers #############");

        List<UserManagementListMemberPojo> members = userManagementListService.searchMembers(params);
        return ResponseEntity.ok(Response.success("Members fetched successfully", members, HttpStatus.OK.value()));
    }

    @GetMapping("/members/count")
    public ResponseEntity<Response<Long>> countMembers(
            @RequestParam Map<String, Object> params
    ) {
        logger.debug("countMembers #############");
        logger.debug("countMembers params={}", params);
        logger.debug("countMembers #############");

        long count = userManagementListService.countMembers(params);
        return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
    }
}
