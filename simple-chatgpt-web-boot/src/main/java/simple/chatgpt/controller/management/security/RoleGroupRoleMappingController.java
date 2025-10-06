package simple.chatgpt.controller.management.security;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.service.management.security.RoleGroupRoleMappingService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/rolegrouprolemappings", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleGroupRoleMappingController {

    private static final Logger logger = LogManager.getLogger(RoleGroupRoleMappingController.class);

    private final RoleGroupRoleMappingService mappingService;

    public RoleGroupRoleMappingController(RoleGroupRoleMappingService mappingService) {
        this.mappingService = mappingService;
        logger.debug("RoleGroupRoleMappingController constructor called, mappingService={}", mappingService);
    }

    // ---------------- CREATE ----------------

    @PostMapping("/add")
    public ResponseEntity<Response<Integer>> insertMapping(@RequestBody RoleGroupRoleMappingPojo mapping) {
        logger.debug("insertMapping called mapping={}", mapping);

        int result = mappingService.insertMapping(Map.of("mapping", mapping));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Mapping inserted successfully", result, HttpStatus.CREATED.value()));
    }

    @PostMapping("/addIfNotExists")
    public ResponseEntity<Response<RoleGroupRoleMappingPojo>> addRoleToGroupIfNotExists(
            @RequestParam Long roleGroupId,
            @RequestParam Long roleId
    ) {
        logger.debug("addRoleToGroupIfNotExists called, roleGroupId={}, roleId={}", roleGroupId, roleId);

        RoleGroupRoleMappingPojo result = mappingService.addRoleToGroupIfNotExists(
                Map.of("roleGroupId", roleGroupId, "roleId", roleId)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Role added to role group if not exists successfully", result, HttpStatus.CREATED.value()));
    }

    // ---------------- DELETE ----------------

    @DeleteMapping("/deleteById")
    public ResponseEntity<Response<Integer>> deleteMappingById(@RequestParam Long id) {
        logger.debug("deleteMappingById called, id={}", id);

        int result = mappingService.deleteMappingById(Map.of("id", id));

        return ResponseEntity.ok(Response.success("Mapping deleted successfully", result, HttpStatus.OK.value()));
    }

    @DeleteMapping("/deleteByGroupAndRole")
    public ResponseEntity<Response<Integer>> deleteMappingByGroupAndRole(
            @RequestParam Long roleGroupId,
            @RequestParam Long roleId
    ) {
        logger.debug("deleteMappingByGroupAndRole called, roleGroupId={}, roleId={}", roleGroupId, roleId);

        int result = mappingService.deleteMappingByGroupAndRole(Map.of("roleGroupId", roleGroupId, "roleId", roleId));

        return ResponseEntity.ok(Response.success("Mapping deleted successfully", result, HttpStatus.OK.value()));
    }

    // ---------------- READ ----------------

    @GetMapping("/listAll")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findAllMappings() {
        logger.debug("findAllMappings called");

        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.findAllMappings();
        logger.debug("findAllMappings result size={}", result.getItems().size());

        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/listByRoleGroup")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findByRoleGroupId(@RequestParam Long roleGroupId) {
        logger.debug("findByRoleGroupId called, roleGroupId={}", roleGroupId);

        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.findByRoleGroupId(Map.of("roleGroupId", roleGroupId));
        logger.debug("findByRoleGroupId result size={}", result.getItems().size());

        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/listByRole")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findByRoleId(@RequestParam Long roleId) {
        logger.debug("findByRoleId called, roleId={}", roleId);

        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.findByRoleId(Map.of("roleId", roleId));
        logger.debug("findByRoleId result size={}", result.getItems().size());

        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    // ---------------- SEARCH / PAGINATION ----------------

    @GetMapping("/findMappings")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findMappings(@RequestParam Map<String, Object> params) {
        logger.debug("findMappings called, params={}", params);

        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.findMappings(params);
        logger.debug("findMappings result size={}", result.getItems().size());

        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/searchMappings")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> searchMappings(@RequestParam Map<String, Object> params) {
        logger.debug("searchMappings called, params={}", params);

        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.searchMappings(params);
        logger.debug("searchMappings result size={}", result.getItems().size());

        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    // ---------------- COUNT ----------------

    @GetMapping("/countMappings")
    public ResponseEntity<Response<Long>> countMappings(@RequestParam Map<String, Object> params) {
        logger.debug("countMappings called, params={}", params);

        long count = mappingService.countMappings(params);
        logger.debug("countMappings result={}", count);

        return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
    }
}

/*
Here’s the updated mapping between JS calls and the controller URLs for your roleGroupRoles.js setup:

JS Call	Controller URL associated with a valid method	Controller Method Exists	Suggestion / Fix
Save mapping (create)	/management/rolegrouprolemappings/add	✅ Yes	Update JS to call /add instead of /create
Save mapping (update)	/management/rolegrouprolemappings/addIfNotExists	✅ Yes	JS can call /addIfNotExists if using "add only if not exists" logic
Delete mapping (by ID)	/management/rolegrouprolemappings/deleteById	✅ Yes	Update JS to call /deleteById instead of /delete
Delete mapping (by group & role)	/management/rolegrouprolemappings/deleteByGroupAndRole	✅ Yes	Add JS method to call /deleteByGroupAndRole if needed
Load mapping by ID	/management/rolegrouprolemappings/listAll + filter	✅ Yes	JS currently uses /get; update to /listAll or /listByRoleGroup with filtering
Load mappings (search / list)	/management/rolegrouprolemappings/searchMappings	✅ Yes	Update JS to call /searchMappings instead of /search
Optional: list all mappings	/management/rolegrouprolemappings/listAll	✅ Yes	Use /listAll for full list if needed
Optional: list by role group	/management/rolegrouprolemappings/listByRoleGroup	✅ Yes	Use /listByRoleGroup if filtering by group
Optional: list by role	/management/rolegrouprolemappings/listByRole	✅ Yes	Use /listByRole if filtering by role
Optional: find mappings with filters	/management/rolegrouprolemappings/findMappings	✅ Yes	Already correct; JS can call /findMappings
Count mappings	/management/rolegrouprolemappings/countMappings	✅ Yes	JS currently not calling; use /countMappings if needed
*/