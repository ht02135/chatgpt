package simple.chatgpt.controller.management;

import java.util.Arrays;
import java.util.HashMap;
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
    public ResponseEntity<Response<PagedResult<UserManagementListPojo>>> searchUserLists(@RequestParam Map<String, Object> params) {
        logger.debug("searchUserLists called");

        Map<String, Object> innerParams = getParamsMap(params);

        int page = 0;
        int size = 20;
        try {
            if (innerParams.get("page") != null) page = Integer.parseInt(innerParams.get("page").toString());
        } catch (NumberFormatException e) {
            logger.warn("Invalid page param {}, defaulting to 0", innerParams.get("page"), e);
        }
        try {
            if (innerParams.get("size") != null) size = Integer.parseInt(innerParams.get("size").toString());
        } catch (NumberFormatException e) {
            logger.warn("Invalid size param {}, defaulting to 20", innerParams.get("size"), e);
        }

        int offset = page * size;
        String sortField = (String) innerParams.getOrDefault("sortField", "id");
        String sortDirection = ((String) innerParams.getOrDefault("sortDirection", "ASC")).toUpperCase();

        innerParams.put("page", page);
        innerParams.put("size", size);
        innerParams.put("offset", offset);
        innerParams.put("limit", size);
        innerParams.put("sortField", sortField);
        innerParams.put("sortDirection", sortDirection);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("searchUserLists param {}={}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        PagedResult<UserManagementListPojo> lists = userManagementListService.searchUserLists(wrapperParam);
        return ResponseEntity.ok(Response.success("Fetched successfully", lists, HttpStatus.OK.value()));
    }

    // ------------------ CREATE LIST WITH MEMBERS ------------------
    @PostMapping("/create")
    public ResponseEntity<Response<UserManagementListPojo>> createList(
            @RequestPart("list") UserManagementListPojo list,
            @RequestPart(value = "members", required = false) UserManagementListMemberPojo[] members
    ) {
        logger.debug("createList called");

        logger.debug("createList list={}", list);
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

            Map<String, Object> innerParams = getParamsMap(params);
            Map<String, Object> wrapperParam = new HashMap<>();
            wrapperParam.put("params", innerParams);

            userManagementListService.createList(wrapperParam);
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
    public ResponseEntity<Response<UserManagementListPojo>> getListById(@RequestParam Long listId) {
        logger.debug("getListById called");
        logger.debug("getListById listId={}", listId);

        Map<String, Object> innerParams = getParamsMap(new HashMap<>());
        innerParams.put("listId", listId);

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        UserManagementListPojo list = userManagementListService.getListById(wrapperParam);
        if (list == null) {
            return ResponseEntity.ok(Response.error("List not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("List fetched successfully", list, HttpStatus.OK.value()));
    }

    // ------------------ GET MEMBERS BY LIST ------------------
    @GetMapping("/members")
    public ResponseEntity<Response<PagedResult<UserManagementListMemberPojo>>> getMembersByListId(@RequestParam Long listId) {
        logger.debug("getMembersByListId called");
        logger.debug("getMembersByListId listId={}", listId);

        Map<String, Object> innerParams = getParamsMap(new HashMap<>());
        innerParams.put("listId", listId);

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        PagedResult<UserManagementListMemberPojo> members = userManagementListService.getMembersByListId(wrapperParam);
        return ResponseEntity.ok(Response.success("Members fetched successfully", members, HttpStatus.OK.value()));
    }

    // ------------------ DELETE LIST ------------------
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteList(@RequestParam Long listId) {
        logger.debug("deleteList called");
        logger.debug("deleteList listId={}", listId);

        Map<String, Object> innerParams = getParamsMap(new HashMap<>());
        innerParams.put("listId", listId);

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        userManagementListService.deleteList(wrapperParam);
        return ResponseEntity.ok(Response.success("List deleted successfully", null, HttpStatus.OK.value()));
    }

    // ------------------ IMPORT LIST ------------------
    @PostMapping("/import")
    public ResponseEntity<Response<UserManagementListPojo>> importList(
            @RequestPart("list") UserManagementListPojo list,
            @RequestPart("file") MultipartFile file
    ) {
        logger.debug("importList called");
        logger.debug("importList list={}", list);
        logger.debug("importList fileName={}", file.getOriginalFilename());

        try (var is = file.getInputStream()) {
            Map<String, Object> params = new HashMap<>();
            params.put("list", list);
            params.put("inputStream", is);
            params.put("originalFileName", file.getOriginalFilename());

            Map<String, Object> innerParams = getParamsMap(params);
            Map<String, Object> wrapperParam = new HashMap<>();
            wrapperParam.put("params", innerParams);

            String filename = file.getOriginalFilename().toLowerCase();
            if (filename.endsWith(".csv")) {
                userManagementListService.importListFromCsv(wrapperParam);
            } else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
                userManagementListService.importListFromExcel(wrapperParam);
            } else {
                return ResponseEntity.badRequest()
                        .body(Response.error("Unsupported file type", null, HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            logger.error("importList failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Import failed: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("List imported successfully", list, HttpStatus.CREATED.value()));
    }

    // ------------------ EXPORT LIST CSV ------------------
    @GetMapping("/export/csv")
    public void exportListToCsv(@RequestParam Long listId, HttpServletResponse response) {
        logger.debug("exportListToCsv called");
        logger.debug("exportListToCsv listId={}", listId);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"list_" + listId + ".csv\"");

        try (var os = response.getOutputStream()) {
            Map<String, Object> innerParams = getParamsMap(new HashMap<>());
            innerParams.put("listId", listId);
            innerParams.put("outputStream", os);

            Map<String, Object> wrapperParam = new HashMap<>();
            wrapperParam.put("params", innerParams);

            userManagementListService.exportListToCsv(wrapperParam);
        } catch (Exception e) {
            logger.error("exportListToCsv failed", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    // ------------------ EXPORT LIST EXCEL ------------------
    @GetMapping("/export/excel")
    public void exportListToExcel(@RequestParam Long listId, HttpServletResponse response) {
        logger.debug("exportListToExcel called");
        logger.debug("exportListToExcel listId={}", listId);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"list_" + listId + ".xlsx\"");

        try (var os = response.getOutputStream()) {
            Map<String, Object> innerParams = getParamsMap(new HashMap<>());
            innerParams.put("listId", listId);
            innerParams.put("outputStream", os);

            Map<String, Object> wrapperParam = new HashMap<>();
            wrapperParam.put("params", innerParams);

            userManagementListService.exportListToExcel(wrapperParam);
        } catch (Exception e) {
            logger.error("exportListToExcel failed", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    // ------------------ SEARCH MEMBERS ------------------
    @GetMapping("/members/search")
    public ResponseEntity<Response<PagedResult<UserManagementListMemberPojo>>> searchMembers(@RequestParam Map<String, Object> params) {
        logger.debug("searchMembers called");

        Map<String, Object> innerParams = getParamsMap(params);

        int page = 0;
        int size = 20;
        try {
            if (innerParams.get("page") != null) page = Integer.parseInt(innerParams.get("page").toString());
        } catch (NumberFormatException e) {
            logger.warn("Invalid page param {}, defaulting to 0", innerParams.get("page"), e);
        }
        try {
            if (innerParams.get("size") != null) size = Integer.parseInt(innerParams.get("size").toString());
        } catch (NumberFormatException e) {
            logger.warn("Invalid size param {}, defaulting to 20", innerParams.get("size"), e);
        }

        int offset = page * size;
        String sortField = (String) innerParams.getOrDefault("sortField", "id");
        String sortDirection = ((String) innerParams.getOrDefault("sortDirection", "ASC")).toUpperCase();

        innerParams.put("page", page);
        innerParams.put("size", size);
        innerParams.put("offset", offset);
        innerParams.put("limit", size);
        innerParams.put("sortField", sortField);
        innerParams.put("sortDirection", sortDirection);

        for (Map.Entry<String, Object> entry : innerParams.entrySet()) {
            logger.debug("searchMembers param {}={}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        PagedResult<UserManagementListMemberPojo> members = userManagementListService.searchMembers(wrapperParam);
        return ResponseEntity.ok(Response.success("Members fetched successfully", members, HttpStatus.OK.value()));
    }

    // ------------------ COUNT MEMBERS ------------------
    @GetMapping("/members/count")
    public ResponseEntity<Response<Long>> countMembers(@RequestParam Map<String, Object> params) {
        logger.debug("countMembers called");

        Map<String, Object> innerParams = getParamsMap(params);
        Map<String, Object> wrapperParam = new HashMap<>();
        wrapperParam.put("params", innerParams);

        long count = userManagementListService.countMembers(wrapperParam);
        return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
    }

    // ------------------ HELPER ------------------
    private Map<String, Object> getParamsMap(Map<String, Object> params) {
        // Always returns a map (even if empty)
        return params != null ? params : new HashMap<>();
    }
}
