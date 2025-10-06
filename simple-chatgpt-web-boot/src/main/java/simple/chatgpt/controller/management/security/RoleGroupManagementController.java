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

import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.service.management.security.RoleGroupManagementService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/rolegroups", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleGroupManagementController {

    private static final Logger logger = LogManager.getLogger(RoleGroupManagementController.class);

    private final RoleGroupManagementService roleGroupService;

    public RoleGroupManagementController(RoleGroupManagementService roleGroupService) {
        this.roleGroupService = roleGroupService;
        logger.debug("RoleGroupManagementController constructor called, roleGroupService={}", roleGroupService);
    }

    // ---------------- CREATE ----------------
    @PostMapping("/insert")
    public ResponseEntity<Response<RoleGroupManagementPojo>> insertRoleGroup(@RequestBody RoleGroupManagementPojo group) {
        logger.debug("insertRoleGroup called, group={}", group);

        Map<String, Object> params = new HashMap<>();
        params.put("group", group);

        RoleGroupManagementPojo inserted = roleGroupService.insertRoleGroup(params);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Role group inserted successfully", inserted, HttpStatus.CREATED.value()));
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/update")
    public ResponseEntity<Response<RoleGroupManagementPojo>> updateRoleGroup(@RequestBody Map<String, Object> params) {
        logger.debug("updateRoleGroup called, params={}", params);

        RoleGroupManagementPojo updated = roleGroupService.updateRoleGroup(params);
        return ResponseEntity.ok(Response.success("Role group updated successfully", updated, HttpStatus.OK.value()));
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/deleteById")
    public ResponseEntity<Response<Void>> deleteRoleGroupById(@RequestParam Long roleGroupId) {
        logger.debug("deleteRoleGroupById called, roleGroupId={}", roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("roleGroupId", roleGroupId);

        roleGroupService.deleteRoleGroupById(params); // service returns void
        return ResponseEntity.ok(Response.success("Role group deleted by ID successfully", null, HttpStatus.OK.value()));
    }

    @DeleteMapping("/deleteByName")
    public ResponseEntity<Response<Void>> deleteRoleGroupByName(@RequestParam String groupName) {
        logger.debug("deleteRoleGroupByName called, groupName={}", groupName);

        Map<String, Object> params = new HashMap<>();
        params.put("groupName", groupName);

        roleGroupService.deleteRoleGroupByName(params); // service returns void
        return ResponseEntity.ok(Response.success("Role group deleted by name successfully", null, HttpStatus.OK.value()));
    }

    // ---------------- READ ----------------
    @GetMapping("/findById")
    public ResponseEntity<Response<RoleGroupManagementPojo>> findRoleGroupById(@RequestParam Long roleGroupId) {
        logger.debug("findRoleGroupById called, roleGroupId={}", roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("roleGroupId", roleGroupId);

        RoleGroupManagementPojo group = roleGroupService.findRoleGroupById(params);
        return ResponseEntity.ok(Response.success("Role group fetched successfully", group, HttpStatus.OK.value()));
    }

    @GetMapping("/findByName")
    public ResponseEntity<Response<RoleGroupManagementPojo>> findRoleGroupByName(@RequestParam String groupName) {
        logger.debug("findRoleGroupByName called, groupName={}", groupName);

        Map<String, Object> params = new HashMap<>();
        params.put("groupName", groupName);

        RoleGroupManagementPojo group = roleGroupService.findRoleGroupByName(params);
        return ResponseEntity.ok(Response.success("Role group fetched successfully", group, HttpStatus.OK.value()));
    }

    // ---------------- LIST ALL ----------------
    @GetMapping("/findAll")
    public ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> findAllRoleGroups() {
        logger.debug("findAllRoleGroups called");

        PagedResult<RoleGroupManagementPojo> result = roleGroupService.findAllRoleGroups();
        logger.debug("findAllRoleGroups result items={}", result.getItems());
        logger.debug("findAllRoleGroups totalCount={}", result.getTotalCount());

        return ResponseEntity.ok(Response.success("All role groups fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/getAll")
    public ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> getAllRoleGroups() {
        logger.debug("getAllRoleGroups called");

        PagedResult<RoleGroupManagementPojo> result = roleGroupService.getAllRoleGroups();
        logger.debug("getAllRoleGroups result items={}", result.getItems());
        logger.debug("getAllRoleGroups totalCount={}", result.getTotalCount());

        return ResponseEntity.ok(Response.success("All role groups fetched successfully", result, HttpStatus.OK.value()));
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @GetMapping("/find")
    public ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> findRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("findRoleGroups called, params={}", params);

        PagedResult<RoleGroupManagementPojo> result = roleGroupService.findRoleGroups(params);
        logger.debug("findRoleGroups result items={}", result.getItems());
        logger.debug("findRoleGroups totalCount={}", result.getTotalCount());

        return ResponseEntity.ok(Response.success("Filtered role groups fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> searchRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("searchRoleGroups called, params={}", params);

        PagedResult<RoleGroupManagementPojo> result = roleGroupService.searchRoleGroups(params);
        logger.debug("searchRoleGroups result items={}", result.getItems());
        logger.debug("searchRoleGroups totalCount={}", result.getTotalCount());

        return ResponseEntity.ok(Response.success("Searched role groups fetched successfully", result, HttpStatus.OK.value()));
    }

    // ---------------- COUNT ----------------
    @GetMapping("/count")
    public ResponseEntity<Response<Long>> countRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("countRoleGroups called, params={}", params);

        long count = roleGroupService.countRoleGroups(params);
        return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
    }
}

/*
HUNG : DONT REMOVE
Heres a comprehensive mapping table for roleGroups.js (and role group mappings) based on your JS calls and the controller methods you provided:

JS Call (roleGroups.js)	Controller URL associated with a valid method	Controller Method Exists	Suggestion / Fix
Save role group (create)	/management/rolegroups/insert	? Yes	Update JS to call /insert instead of /create
Save role group (update)	/management/rolegroups/update	? Yes	JS already uses correct URL; ensure roleGroupId query param is sent
Delete role group	/management/rolegroups/deleteById	? Yes	Update JS to call /deleteById instead of /delete
Load role group by ID	/management/rolegroups/findById	? Yes	Update JS to call /findById instead of /get
Load role groups (search / list)	/management/rolegroups/searchRoleGroups	? Yes	Update JS to call /search instead of /searchRoleGroups (controller uses /search)
Optional: list all role groups	/management/rolegroups/findAll	? Yes	Use /findAll if needed for full role group list
Optional: get all role groups	/management/rolegroups/getAll	? Yes	Use /getAll if needed
Optional: find role groups with filters	/management/rolegroups/findRoles	? Yes	Already correct; no change needed
Count role groups	/management/rolegroups/count	? Yes	JS currently not calling; use if needed
*/