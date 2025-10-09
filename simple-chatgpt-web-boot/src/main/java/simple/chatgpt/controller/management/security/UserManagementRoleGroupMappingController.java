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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.service.management.security.UserManagementRoleGroupMappingService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.Response;
import simple.chatgpt.util.SafeConverter;

@RestController
@RequestMapping(value = "/management/userrolegroups", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementRoleGroupMappingController implements UserManagementRoleGroupMappingControllerApi {

    private static final Logger logger = LogManager.getLogger(UserManagementRoleGroupMappingController.class);

    private final UserManagementRoleGroupMappingService mappingService;

    public UserManagementRoleGroupMappingController(UserManagementRoleGroupMappingService mappingService) {
        this.mappingService = mappingService;
        logger.debug("UserRoleGroupManagementController constructor called, mappingService={}", mappingService);
    }

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @PostMapping("/create")
    public ResponseEntity<Response<UserManagementRoleGroupMappingPojo>> create(
            @RequestBody(required = false) UserManagementRoleGroupMappingPojo mapping) {
        logger.debug("create called");
        logger.debug("create mapping={}", mapping);

        if (mapping == null) {
            logger.debug("create: missing payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        UserManagementRoleGroupMappingPojo created = mappingService.insertUserRoleGroup(ParamWrapper.wrap("mapping", mapping));

        logger.debug("create return={}", created);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("User role group mapping created successfully", created, HttpStatus.CREATED.value()));
    }

    @PutMapping("/update")
    public ResponseEntity<Response<UserManagementRoleGroupMappingPojo>> update(
            @RequestParam(required = false) Long id,
            @RequestBody(required = false) UserManagementRoleGroupMappingPojo mapping) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update mapping={}", mapping);

        if (id == null) {
            logger.debug("update: missing id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        if (mapping == null) {
            logger.debug("update: missing payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        mapping.setId(id);
        logger.debug("update set mapping.id={}", mapping.getId());

        UserManagementRoleGroupMappingPojo updated = mappingService.updateUserRoleGroup(ParamWrapper.wrap("mapping", mapping));

        logger.debug("update return={}", updated);
        return ResponseEntity.ok(Response.success("User role group mapping updated successfully", updated, HttpStatus.OK.value()));
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> search(
            @RequestParam Map<String, String> params) {

        logger.debug("search called");
        logger.debug("search params={}", params);

        // Add defaults only for XML-used params
        if (!params.containsKey("page")) params.put("page", "0");
        if (!params.containsKey("size")) params.put("size", "20");
        int page = SafeConverter.toIntOrDefault(params.get("page"), 0);
        int size = SafeConverter.toIntOrDefault(params.get("size"), 20);
        int offset = page * size;

        if (!params.containsKey("offset")) params.put("offset", String.valueOf(offset));
        if (!params.containsKey("limit")) params.put("limit", String.valueOf(size));

        if (!params.containsKey("sortField")) params.put("sortField", "id");
        if (!params.containsKey("sortDirection")) params.put("sortDirection", "ASC");

        PagedResult<UserManagementRoleGroupMappingPojo> result = mappingService.search(params);
        return ResponseEntity.ok(Response.success("Fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/get")
    public ResponseEntity<Response<UserManagementRoleGroupMappingPojo>> get(
            @RequestParam(required = false) Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);

        if (id == null) {
            logger.debug("get: missing id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        UserManagementRoleGroupMappingPojo result = mappingService.findById(ParamWrapper.wrap("id", id));
        return ResponseEntity.ok(Response.success("Search results", result, HttpStatus.OK.value()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> delete(
            @RequestParam(required = false) Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);

        if (id == null) {
            logger.debug("delete: missing id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        mappingService.delete(id);
        return ResponseEntity.ok(Response.success("Deleted successfully", null, HttpStatus.OK.value()));
    }

    // ======= OTHER METHODS =======
    
    // ---------------- CREATE ----------------

    @PostMapping("/insertUserRoleGroup")
    public ResponseEntity<Response<UserManagementRoleGroupMappingPojo>> insertUserRoleGroup(
            @RequestParam Long userId,
            @RequestParam Long roleGroupId) {

        logger.debug("insertUserRoleGroup START");
        logger.debug("insertUserRoleGroup userId={}", userId);
        logger.debug("insertUserRoleGroup roleGroupId={}", roleGroupId);

        if (userId == null || roleGroupId == null) {
            logger.debug("insertUserRoleGroup: missing userId or roleGroupId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing userId or roleGroupId", null, HttpStatus.BAD_REQUEST.value()));
        }

        // Build mapping POJO
        UserManagementRoleGroupMappingPojo mapping = new UserManagementRoleGroupMappingPojo();
        mapping.setUserId(userId);
        mapping.setRoleGroupId(roleGroupId);

        UserManagementRoleGroupMappingPojo created = mappingService.insertUserRoleGroup(ParamWrapper.wrap("mapping", mapping));

        logger.debug("insertUserRoleGroup return={}", created);
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

        if (id == null || userId == null || roleGroupId == null) {
            logger.debug("updateUserRoleGroup: missing id, userId, or roleGroupId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id, userId, or roleGroupId", null, HttpStatus.BAD_REQUEST.value()));
        }

        // Build mapping POJO with id
        UserManagementRoleGroupMappingPojo mapping = new UserManagementRoleGroupMappingPojo();
        mapping.setId(id);
        mapping.setUserId(userId);
        mapping.setRoleGroupId(roleGroupId);

        UserManagementRoleGroupMappingPojo updated = mappingService.updateUserRoleGroup(ParamWrapper.wrap("mapping", mapping));

        logger.debug("updateUserRoleGroup return={}", updated);
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

        logger.debug("findAllUserRoleGroups return={}", paged);
        return ResponseEntity.ok(Response.success("All user role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/findByUserId")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findByUserId(@RequestParam Long userId) {
        logger.debug("findByUserId START");
        logger.debug("findByUserId userId={}", userId);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.findByUserId(params);

        logger.debug("findByUserId return={}", paged);
        return ResponseEntity.ok(Response.success("Mappings for user fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/findByRoleGroupId")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findByRoleGroupId(@RequestParam Long roleGroupId) {
        logger.debug("findByRoleGroupId START");
        logger.debug("findByRoleGroupId roleGroupId={}", roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("roleGroupId", roleGroupId);

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.findByRoleGroupId(params);

        logger.debug("findByRoleGroupId return={}", paged);
        return ResponseEntity.ok(Response.success("Mappings for role group fetched successfully", paged, HttpStatus.OK.value()));
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @GetMapping("/findUserRoleGroups")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> findUserRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("findUserRoleGroups START");
        logger.debug("findUserRoleGroups params={}", params);

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.findUserRoleGroups(params);

        logger.debug("findUserRoleGroups return={}", paged);
        return ResponseEntity.ok(Response.success("Paged user role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    @GetMapping("/searchUserRoleGroups")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> searchUserRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("searchUserRoleGroups START");
        logger.debug("searchUserRoleGroups params={}", params);

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.searchUserRoleGroups(params);

        logger.debug("searchUserRoleGroups return={}", paged);
        return ResponseEntity.ok(Response.success("Search user role groups fetched successfully", paged, HttpStatus.OK.value()));
    }

    // ---------------- COUNT ----------------
    @GetMapping("/countUserRoleGroups")
    public ResponseEntity<Response<Long>> countUserRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("countUserRoleGroups START");
        logger.debug("countUserRoleGroups params={}", params);

        long count = mappingService.countUserRoleGroups(params);

        logger.debug("countUserRoleGroups return={}", count);
        return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
    }
}
