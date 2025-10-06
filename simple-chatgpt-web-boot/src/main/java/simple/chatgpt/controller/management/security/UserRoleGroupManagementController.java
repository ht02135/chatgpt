package simple.chatgpt.controller.management.security;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.service.management.security.UserManagementRoleGroupMappingService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/userrolegroups", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRoleGroupManagementController {

    private static final Logger logger = LogManager.getLogger(UserRoleGroupManagementController.class);

    private final UserManagementRoleGroupMappingService mappingService;

    public UserRoleGroupManagementController(UserManagementRoleGroupMappingService mappingService) {
        this.mappingService = mappingService;
        logger.debug("UserRoleGroupManagementController constructor called, mappingService={}", mappingService);
    }

    // ---------------- CREATE ----------------
    @PostMapping("/insertUserRoleGroup")
    public ResponseEntity<Response<UserManagementRoleGroupMappingPojo>> insertUserRoleGroup(
            @RequestParam Long userId,
            @RequestParam Long roleGroupId) {
        logger.debug("insertUserRoleGroup called, userId={}, roleGroupId={}", userId, roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("roleGroupId", roleGroupId);

        UserManagementRoleGroupMappingPojo created = mappingService.insertUserRoleGroup(params);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("User role group mapping created successfully", created, HttpStatus.CREATED.value()));
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/updateUserRoleGroup")
    public ResponseEntity<Response<UserManagementRoleGroupMappingPojo>> updateUserRoleGroup(
            @RequestParam Long id,
            @RequestParam Long userId,
            @RequestParam Long roleGroupId) {
        logger.debug("updateUserRoleGroup called, id={}, userId={}, roleGroupId={}", id, userId, roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("userId", userId);
        params.put("roleGroupId", roleGroupId);

        UserManagementRoleGroupMappingPojo updated = mappingService.updateUserRoleGroup(params);
        return ResponseEntity.ok(Response.success("User role group mapping updated successfully", updated, HttpStatus.OK.value()));
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/deleteUserRoleGroupById")
    public ResponseEntity<Response<Void>> deleteUserRoleGroupById(@RequestParam Long id) {
        logger.debug("deleteUserRoleGroupById called, id={}", id);

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        mappingService.deleteUserRoleGroupById(params);
        return ResponseEntity.ok(Response.success("Mapping deleted successfully", null, HttpStatus.OK.value()));
    }

    @DeleteMapping("/deleteUserRoleGroupByUserAndGroup")
    public ResponseEntity<Response<Void>> deleteUserRoleGroupByUserAndGroup(
            @RequestParam Long userId,
            @RequestParam Long roleGroupId) {
        logger.debug("deleteUserRoleGroupByUserAndGroup called, userId={}, roleGroupId={}", userId, roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("roleGroupId", roleGroupId);
        mappingService.deleteUserRoleGroupByUserAndGroup(params);
        return ResponseEntity.ok(Response.success("Mapping deleted successfully", null, HttpStatus.OK.value()));
    }

    // ---------------- READ ----------------
    @GetMapping("/findAllUserRoleGroups")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findAllUserRoleGroups() {
        logger.debug("findAllUserRoleGroups called");

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.findAllUserRoleGroups();
        return ResponseEntity.ok(Response.success("All user role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/findByUserId")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findByUserId(@RequestParam Long userId) {
        logger.debug("findByUserId called, userId={}", userId);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.findByUserId(params);
        return ResponseEntity.ok(Response.success("Mappings for user fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/findByRoleGroupId")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findByRoleGroupId(@RequestParam Long roleGroupId) {
        logger.debug("findByRoleGroupId called, roleGroupId={}", roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("roleGroupId", roleGroupId);
        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.findByRoleGroupId(params);
        return ResponseEntity.ok(Response.success("Mappings for role group fetched successfully", paged, HttpStatus.OK.value()));
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @GetMapping("/findUserRoleGroups")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findUserRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("findUserRoleGroups called, params={}", params);

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.findUserRoleGroups(params);
        return ResponseEntity.ok(Response.success("Paged user role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/searchUserRoleGroups")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> searchUserRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("searchUserRoleGroups called, params={}", params);

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.searchUserRoleGroups(params);
        return ResponseEntity.ok(Response.success("Search user role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    // ---------------- COUNT ----------------
    @GetMapping("/countUserRoleGroups")
    public ResponseEntity<Response<Long>> countUserRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("countUserRoleGroups called, params={}", params);

        long count = mappingService.countUserRoleGroups(params);
        return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
    }
}

/*
HUNG : DoNT REMOVE
JS method name	JS API call	Controller API	Do we need to update JS API call	Suggestion
Save mapping (create)	/add	/management/userrolegroups/insertUserRoleGroup	Yes	Update JS to call /insertUserRoleGroup instead of /add
Save mapping (update)	/update	/management/userrolegroups/updateUserRoleGroup	Yes	JS can call /updateUserRoleGroup if you implement update logic
Delete mapping (by ID)	/delete	/management/userrolegroups/deleteUserRoleGroupById	Yes	Update JS to call /deleteUserRoleGroupById instead of /delete
Delete mapping (by user & group)	N/A	/management/userrolegroups/deleteUserRoleGroupByUserAndGroup	Yes	Add JS method to call /deleteUserRoleGroupByUserAndGroup if needed
Load mapping by ID	/get	/management/userrolegroups/findByUserId or /findByRoleGroupId	Yes	Update JS to call /findByUserId or /findByRoleGroupId instead of /get
Load mappings (search/list)	/search	/management/userrolegroups/findUserRoleGroups	Yes	Update JS to call /findUserRoleGroups instead of /search
Optional: list all mappings	N/A	/management/userrolegroups/findAllUserRoleGroups	No	Could add a JS loadAll method if needed
Optional: list by user	N/A	/management/userrolegroups/findByUserId	No	Use this if filtering by user
Optional: list by role group	N/A	/management/userrolegroups/findByRoleGroupId	No	Use this if filtering by role group
Optional: search mappings with filters	N/A	/management/userrolegroups/searchUserRoleGroups	No	Already correct; JS can call this
Count mappings	N/A	/management/userrolegroups/countUserRoleGroups	No	Could add if needed
*/