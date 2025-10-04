package simple.chatgpt.controller.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
import simple.chatgpt.service.management.PropertyManagementService;
import simple.chatgpt.service.management.UserManagementListService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.PropertyKey;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/userlists", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementListController {

    private static final Logger logger = LogManager.getLogger(UserManagementListController.class);

    private final UserManagementListService userManagementListService;
    private final PropertyManagementService propertyService; 

    // Constructor injection
    public UserManagementListController(UserManagementListService userManagementListService,
    		PropertyManagementService propertyService) {
        this.userManagementListService = userManagementListService;
        this.propertyService = propertyService;
    }

    // ------------------ LIST SEARCH ------------------
    @GetMapping
    public ResponseEntity<Response<PagedResult<UserManagementListPojo>>> searchUserLists(
            @RequestParam Map<String, Object> params
    ) {
        logger.debug("searchUserLists called with params={}", params);

        int page = 0, size = 20;
        try { page = getInt(params.getOrDefault("page", "0")); }
        catch (Exception e) { logger.warn("Invalid page param {}, defaulting to 0", params.get("page"), e); }
        try { size = getInt(params.getOrDefault("size", "20")); }
        catch (Exception e) { logger.warn("Invalid size param {}, defaulting to 20", params.get("size"), e); }

        int offset = page * size;
        String sortField = (String) params.getOrDefault("sortField", "id");
        String sortDirection = ((String) params.getOrDefault("sortDirection", "ASC")).toUpperCase();

        params.put("page", page);
        params.put("size", size);
        params.put("offset", offset);
        params.put("limit", size);
        params.put("sortField", sortField);
        params.put("sortDirection", sortDirection);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            logger.debug("searchUserLists param {}={}", entry.getKey(), entry.getValue());
        }

        PagedResult<UserManagementListPojo> lists = userManagementListService.searchUserLists(params);
        return ResponseEntity.ok(Response.success("Fetched successfully", lists, HttpStatus.OK.value()));
    }

    // ------------------ CREATE LIST WITH MEMBERS ------------------
    @PostMapping("/create")
    public ResponseEntity<Response<UserManagementListPojo>> createList(
            @RequestPart("list") UserManagementListPojo list,
            @RequestPart(value = "members", required = false) UserManagementListMemberPojo[] members
    ) {
        logger.debug("createList called #############");
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
            logger.debug("createList params={}", params);
            userManagementListService.createList(params);
        } catch (Exception e) {
            logger.error("createList failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Create failed: " + e.getMessage(), null, 500));
        }
        
        logger.debug("createList #############");
        logger.debug("createList DONE!!!");
        logger.debug("createList #############");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("List created successfully", list, HttpStatus.CREATED.value()));
    }

    // ------------------ GET LIST BY ID ------------------
    @GetMapping("/get")
    public ResponseEntity<Response<UserManagementListPojo>> getListById(@RequestParam Long listId) {
        logger.debug("getListById #############");
        logger.debug("getListById listId={}", listId);
        logger.debug("getListById #############");

        Map<String, Object> params = new HashMap<>();
        params.put("listId", listId);

        UserManagementListPojo list = userManagementListService.getListById(params);
        if (list == null) {
            return ResponseEntity.ok(Response.error("List not found", null, HttpStatus.NOT_FOUND.value()));
        }
        logger.debug("getListById #############");
        logger.debug("getListById list={}", list);
        logger.debug("getListById #############");

        return ResponseEntity.ok(Response.success("List fetched successfully", list, HttpStatus.OK.value()));
    }

    // ------------------ GET MEMBERS BY LIST ------------------
    @GetMapping("/members")
    public ResponseEntity<Response<PagedResult<UserManagementListMemberPojo>>> getMembersByListId(@RequestParam Long listId) {
        logger.debug("getMembersByListId called #############");
        logger.debug("getMembersByListId listId={}", listId);

        Map<String, Object> params = new HashMap<>();
        params.put("listId", listId);

        PagedResult<UserManagementListMemberPojo> members = userManagementListService.getMembersByListId(params);
        return ResponseEntity.ok(Response.success("Members fetched successfully", members, HttpStatus.OK.value()));
    }

    // ------------------ DELETE LIST ------------------
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteList(@RequestParam Long listId) {
        logger.debug("deleteList called #############");
        logger.debug("deleteList listId={}", listId);

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
        logger.debug("importList called #############");
        logger.debug("importList list={}", list);
        logger.debug("importList fileName={}", file.getOriginalFilename());

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
                return ResponseEntity.badRequest().body(Response.error("Unsupported file type", null, HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            logger.error("importList failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Import failed: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("List imported successfully", list, HttpStatus.CREATED.value()));
    }

    // ------------------ EXPORT LIST ------------------
    @GetMapping("/export/csv")
    public void exportListToCsv(@RequestParam Long listId, HttpServletResponse response) {
        logger.debug("exportListToCsv called #############");
        logger.debug("exportListToCsv listId={}", listId);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"list_" + listId + ".csv\"");

        try (var os = response.getOutputStream()) {
            Map<String, Object> params = new HashMap<>();
            params.put("listId", listId);
            params.put("outputStream", os);
            userManagementListService.exportListToCsv(params);
        } catch (Exception e) {
            logger.error("exportListToCsv failed", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @GetMapping("/export/excel")
    public void exportListToExcel(@RequestParam Long listId, HttpServletResponse response) {
        logger.debug("exportListToExcel called #############");
        logger.debug("exportListToExcel listId={}", listId);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"list_" + listId + ".xlsx\"");

        try (var os = response.getOutputStream()) {
            Map<String, Object> params = new HashMap<>();
            params.put("listId", listId);
            params.put("outputStream", os);
            userManagementListService.exportListToExcel(params);
        } catch (Exception e) {
            logger.error("exportListToExcel failed", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    
    @GetMapping("/download/sample")
    public void downloadSampleCsv(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("downloadSampleCsv called #############");

        String sampleCSVRelativePath = "/management/data/user_lists/test_user_lists_1.csv"; // fallback default

        try {
            sampleCSVRelativePath = propertyService.getString(PropertyKey.SAMPLE_CSV_RELATIVE_PATH);
            logger.debug("After propertyService.getString: sampleCSVRelativePath={}", sampleCSVRelativePath);
        } catch (Exception e) {
            logger.error("Exception fetching SAMPLE_CSV_RELATIVE_PATH property, using default {}", sampleCSVRelativePath, e);
        }

        try {
            String absolutePath = request.getServletContext().getRealPath(sampleCSVRelativePath);
            logger.debug("downloadSampleCsv absolutePath={}", absolutePath);

            File file = new File(absolutePath);
            if (!file.exists()) {
                logger.error("downloadSampleCsv file not found at {}", absolutePath);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

            try (InputStream is = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                is.transferTo(os);
                os.flush();
            }
        } catch (Exception e) {
            logger.error("downloadSampleCsv failed", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // ------------------ SEARCH MEMBERS ------------------
    @GetMapping("/members/search")
    public ResponseEntity<Response<PagedResult<UserManagementListMemberPojo>>> searchMembers(
            @RequestParam Map<String, Object> params
    ) {
        logger.debug("searchMembers called #############");
        logger.debug("searchMembers params={}", params);

        int page = 0;
        int size = 20;
        try {
            if (params.get("page") != null) page = Integer.parseInt(params.get("page").toString());
        } catch (NumberFormatException e) {
            logger.warn("Invalid page param {}, defaulting to 0", params.get("page"), e);
        }
        try {
            if (params.get("size") != null) size = Integer.parseInt(params.get("size").toString());
        } catch (NumberFormatException e) {
            logger.warn("Invalid size param {}, defaulting to 20", params.get("size"), e);
        }

        int offset = page * size;
        String sortField = (String) params.getOrDefault("sortField", "id");
        String sortDirection = ((String) params.getOrDefault("sortDirection", "ASC")).toUpperCase();

        Map<String, Object> serviceParams = new HashMap<>(params);
        serviceParams.put("page", page);
        serviceParams.put("size", size);
        serviceParams.put("offset", offset);
        serviceParams.put("limit", size);
        serviceParams.put("sortField", sortField);
        serviceParams.put("sortDirection", sortDirection);

        for (Map.Entry<String, Object> entry : serviceParams.entrySet()) {
            logger.debug("searchMembers param {}={}", entry.getKey(), entry.getValue());
        }

        PagedResult<UserManagementListMemberPojo> members = userManagementListService.searchMembers(serviceParams);
        return ResponseEntity.ok(Response.success("Members fetched successfully", members, HttpStatus.OK.value()));
    }

    @GetMapping("/members/count")
    public ResponseEntity<Response<Long>> countMembers(@RequestParam Map<String, Object> params) {
        logger.debug("countMembers called #############");
        logger.debug("countMembers params={}", params);

        Map<String, Object> serviceParams = new HashMap<>(params);
        if (params.get("listId") != null) {
            try {
                serviceParams.put("listId", Long.parseLong(params.get("listId").toString()));
            } catch (NumberFormatException e) {
                logger.warn("Invalid listId {}, defaulting to null", params.get("listId"), e);
            }
        }

        for (Map.Entry<String, Object> entry : serviceParams.entrySet()) {
            logger.debug("countMembers param {}={}", entry.getKey(), entry.getValue());
        }

        long count = userManagementListService.countMembers(serviceParams);
        return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
    }
    
    // ------------------ Helpers ------------------
    private int getInt(Object object) {
        if (object == null) {
            return 0; // Treat null as 0
        }

        if (object instanceof Number) {
            return ((Number) object).intValue();
        } else if (object instanceof String) {
            String s = (String) object;
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                // Log the warning using the assumed logger
                logger.warn("Invalid integer value: '{}', defaulting to 0", s, e);
                return 0;
            }
        }

        // Handle any other unexpected Object type by trying to parse its string representation
        // or by simply returning 0. Given the original logic, returning 0 is safer.
        return 0;
    }
}
