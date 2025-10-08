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
public class UserRoleGroupManagementController implements UserRoleGroupManagementControllerApi {

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

        logger.debug("insertUserRoleGroup START");
        logger.debug("insertUserRoleGroup userId={}", userId);
        logger.debug("insertUserRoleGroup roleGroupId={}", roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("roleGroupId", roleGroupId);

        UserManagementRoleGroupMappingPojo created = mappingService.insertUserRoleGroup(params);

        logger.debug("insertUserRoleGroup created={}", created);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("User role group mapping created successfully", created, HttpStatus.CREATED.value()));
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/updateUserRoleGroup")
    public ResponseEntity<Response<UserManagementRoleGroupMappingPojo>> updateUserRoleGroup(
            @RequestParam Long id,
            @RequestParam Long userId,
            @RequestParam Long roleGroupId) {

        logger.debug("updateUserRoleGroup START");
        logger.debug("updateUserRoleGroup id={}", id);
        logger.debug("updateUserRoleGroup userId={}", userId);
        logger.debug("updateUserRoleGroup roleGroupId={}", roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("userId", userId);
        params.put("roleGroupId", roleGroupId);

        UserManagementRoleGroupMappingPojo updated = mappingService.updateUserRoleGroup(params);

        logger.debug("updateUserRoleGroup updated={}", updated);
        return ResponseEntity.ok(Response.success("User role group mapping updated successfully", updated, HttpStatus.OK.value()));
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/deleteUserRoleGroupById")
    public ResponseEntity<Response<Void>> deleteUserRoleGroupById(@RequestParam Long id) {
        logger.debug("deleteUserRoleGroupById START");
        logger.debug("deleteUserRoleGroupById id={}", id);

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        mappingService.deleteUserRoleGroupById(params);
        logger.debug("deleteUserRoleGroupById DONE");

        return ResponseEntity.ok(Response.success("Mapping deleted successfully", null, HttpStatus.OK.value()));
    }

    @DeleteMapping("/deleteUserRoleGroupByUserAndGroup")
    public ResponseEntity<Response<Void>> deleteUserRoleGroupByUserAndGroup(
            @RequestParam Long userId,
            @RequestParam Long roleGroupId) {

        logger.debug("deleteUserRoleGroupByUserAndGroup START");
        logger.debug("deleteUserRoleGroupByUserAndGroup userId={}", userId);
        logger.debug("deleteUserRoleGroupByUserAndGroup roleGroupId={}", roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("roleGroupId", roleGroupId);

        mappingService.deleteUserRoleGroupByUserAndGroup(params);
        logger.debug("deleteUserRoleGroupByUserAndGroup DONE");

        return ResponseEntity.ok(Response.success("Mapping deleted successfully", null, HttpStatus.OK.value()));
    }

    // ---------------- READ ----------------
    @GetMapping("/findAllUserRoleGroups")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findAllUserRoleGroups() {
        logger.debug("findAllUserRoleGroups START");

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.findAllUserRoleGroups();

        logger.debug("findAllUserRoleGroups fetched={}", paged);
        return ResponseEntity.ok(Response.success("All user role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/findByUserId")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findByUserId(@RequestParam Long userId) {
        logger.debug("findByUserId START");
        logger.debug("findByUserId userId={}", userId);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.findByUserId(params);

        logger.debug("findByUserId fetched={}", paged);
        return ResponseEntity.ok(Response.success("Mappings for user fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/findByRoleGroupId")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findByRoleGroupId(@RequestParam Long roleGroupId) {
        logger.debug("findByRoleGroupId START");
        logger.debug("findByRoleGroupId roleGroupId={}", roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("roleGroupId", roleGroupId);

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.findByRoleGroupId(params);

        logger.debug("findByRoleGroupId fetched={}", paged);
        return ResponseEntity.ok(Response.success("Mappings for role group fetched successfully", paged, HttpStatus.OK.value()));
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @GetMapping("/findUserRoleGroups")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findUserRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("findUserRoleGroups START");
        logger.debug("findUserRoleGroups params={}", params);

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.findUserRoleGroups(params);

        logger.debug("findUserRoleGroups fetched={}", paged);
        return ResponseEntity.ok(Response.success("Paged user role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/searchUserRoleGroups")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> searchUserRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("searchUserRoleGroups START");
        logger.debug("searchUserRoleGroups params={}", params);

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.searchUserRoleGroups(params);

        logger.debug("searchUserRoleGroups fetched={}", paged);
        return ResponseEntity.ok(Response.success("Search user role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    // ---------------- COUNT ----------------
    @GetMapping("/countUserRoleGroups")
    public ResponseEntity<Response<Long>> countUserRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("countUserRoleGroups START");
        logger.debug("countUserRoleGroups params={}", params);

        long count = mappingService.countUserRoleGroups(params);

        logger.debug("countUserRoleGroups fetched={}", count);
        return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
    }
}
