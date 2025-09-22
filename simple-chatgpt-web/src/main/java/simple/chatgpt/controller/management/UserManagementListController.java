package simple.chatgpt.controller.management;

import java.util.Arrays;
import java.util.List;

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
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/userlists", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementListController {

    private static final Logger logger = LogManager.getLogger(UserManagementListController.class);

    private final UserManagementListService userManagementListService;

    public UserManagementListController(UserManagementListService userManagementListService) {
        this.userManagementListService = userManagementListService;
    }
    
    // ➕ CREATE LIST WITH MEMBERS
    @PostMapping("/create")
    public ResponseEntity<Response<UserManagementListPojo>> createList(
            @RequestPart("list") UserManagementListPojo list,
            @RequestPart(value = "members", required = false) UserManagementListMemberPojo[] members
    ) {
    	logger.debug("#############");
    	logger.debug("createList list={}", list);
    	logger.debug("#############");
        if(list != null) {
            logger.debug("createList list.userListName={}", list.getUserListName());
            logger.debug("createList list.description={}", list.getDescription());
        }

        if(members != null) {
            for(UserManagementListMemberPojo m : members) {
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
            // Convert array to list for service call
            userManagementListService.createList(list, members != null ? Arrays.asList(members) : null);
        } catch(Exception e) {
            logger.error("createList failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Create failed: " + e.getMessage(), null, 500));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("List created successfully", list, HttpStatus.CREATED.value()));
    }


    // 📖 GET LIST BY ID
    @GetMapping("/get")
    public ResponseEntity<Response<UserManagementListPojo>> getList(@RequestParam Long id) {
    	logger.debug("#############");
    	logger.debug("getList id={}", id);
    	logger.debug("#############");

        UserManagementListPojo list = userManagementListService.getListById(id);
        if (list == null) {
            return ResponseEntity.ok(Response.error("List not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("List fetched successfully", list, HttpStatus.OK.value()));
    }

    // 📖 GET MEMBERS OF LIST
    @GetMapping("/members")
    public ResponseEntity<Response<List<UserManagementListMemberPojo>>> getMembers(@RequestParam Long listId) {
    	logger.debug("#############");
    	logger.debug("getMembers listId={}", listId);
    	logger.debug("#############");

        List<UserManagementListMemberPojo> members = userManagementListService.getMembersByListId(listId);
        return ResponseEntity.ok(Response.success("Members fetched successfully", members, HttpStatus.OK.value()));
    }

    // 🗑 DELETE LIST
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteList(@RequestParam Long listId) {
    	logger.debug("#############");
    	logger.debug("deleteList listId={}", listId);
    	logger.debug("#############");

        userManagementListService.deleteList(listId);
        return ResponseEntity.ok(Response.success("List deleted successfully", null, HttpStatus.OK.value()));
    }

    // 📥 IMPORT LIST FROM CSV/EXCEL
    @PostMapping("/import")
    public ResponseEntity<Response<UserManagementListPojo>> importList(
            @RequestPart("list") UserManagementListPojo list,
            @RequestPart("file") MultipartFile file
    ) {
    	logger.debug("#############");
    	logger.debug("importList list={}", list);
        logger.debug("importList fileName={}", file.getOriginalFilename());
        logger.debug("#############");

        try (var is = file.getInputStream()) {
            String filename = file.getOriginalFilename().toLowerCase();
            if (filename.endsWith(".csv")) {
                userManagementListService.importListFromCsv(is, list, file.getOriginalFilename());
            } else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
                userManagementListService.importListFromExcel(is, list, file.getOriginalFilename());
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

    // 📤 EXPORT LIST TO CSV
    @GetMapping("/export/csv")
    public void exportListToCsv(@RequestParam Long listId, javax.servlet.http.HttpServletResponse response) {
    	logger.debug("#############");
    	logger.debug("exportListToCsv listId={}", listId);
    	logger.debug("#############");
    	
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"list_" + listId + ".csv\"");

        try (var os = response.getOutputStream()) {
            userManagementListService.exportListToCsv(listId, os);
        } catch (Exception e) {
            logger.error("exportListToCsv failed", e);
        }
    }

    // 📤 EXPORT LIST TO EXCEL
    @GetMapping("/export/excel")
    public void exportListToExcel(@RequestParam Long listId, javax.servlet.http.HttpServletResponse response) {
    	logger.debug("#############");
    	logger.debug("exportListToExcel listId={}", listId);
    	logger.debug("#############");
    	
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"list_" + listId + ".xlsx\"");

        try (var os = response.getOutputStream()) {
            userManagementListService.exportListToExcel(listId, os);
        } catch (Exception e) {
            logger.error("exportListToExcel failed", e);
        }
    }
}
