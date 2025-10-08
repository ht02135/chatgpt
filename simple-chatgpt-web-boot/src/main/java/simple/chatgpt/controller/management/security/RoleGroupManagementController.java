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
public class RoleGroupManagementController implements RoleGroupManagementControllerApi {

    private static final Logger logger = LogManager.getLogger(RoleGroupManagementController.class);

    private final RoleGroupManagementService roleGroupService;

    public RoleGroupManagementController(RoleGroupManagementService roleGroupService) {
        this.roleGroupService = roleGroupService;
        logger.debug("RoleGroupManagementController constructor called, roleGroupService={}", roleGroupService);
    }

    // ---------------- CREATE ----------------
    @PostMapping("/insert")
    public ResponseEntity<Response<RoleGroupManagementPojo>> insertRoleGroup(@RequestBody RoleGroupManagementPojo group) {
        logger.debug("insertRoleGroup START");
        logger.debug("insertRoleGroup group={}", group);

        Map<String, Object> params = new HashMap<>();
        params.put("group", group);

        RoleGroupManagementPojo inserted = roleGroupService.insertRoleGroup(params);

        logger.debug("insertRoleGroup return={}", inserted);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Role group inserted successfully", inserted, HttpStatus.CREATED.value()));
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/update")
    public ResponseEntity<Response<RoleGroupManagementPojo>> updateRoleGroup(@RequestBody Map<String, Object> params) {
        logger.debug("updateRoleGroup START");
        logger.debug("updateRoleGroup params={}", params);

        RoleGroupManagementPojo updated = roleGroupService.updateRoleGroup(params);

        logger.debug("updateRoleGroup return={}", updated);
        return ResponseEntity.ok(Response.success("Role group updated successfully", updated, HttpStatus.OK.value()));
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/deleteById")
    public ResponseEntity<Response<Void>> deleteRoleGroupById(@RequestParam Long roleGroupId) {
        logger.debug("deleteRoleGroupById START");
        logger.debug("deleteRoleGroupById roleGroupId={}", roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("roleGroupId", roleGroupId);

        roleGroupService.deleteRoleGroupById(params);

        logger.debug("deleteRoleGroupById DONE");
        return ResponseEntity.ok(Response.success("Role group deleted by ID successfully", null, HttpStatus.OK.value()));
    }

    // ---------------- READ ----------------
    @GetMapping("/findById")
    public ResponseEntity<Response<RoleGroupManagementPojo>> findRoleGroupById(@RequestParam Long roleGroupId) {
        logger.debug("findRoleGroupById START");
        logger.debug("findRoleGroupById roleGroupId={}", roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("roleGroupId", roleGroupId);

        RoleGroupManagementPojo group = roleGroupService.findRoleGroupById(params);

        logger.debug("findRoleGroupById return={}", group);
        return ResponseEntity.ok(Response.success("Role group fetched successfully", group, HttpStatus.OK.value()));
    }

    // ---------------- LIST ALL ----------------
    @GetMapping("/findAll")
    public ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> findAllRoleGroups() {
        logger.debug("findAllRoleGroups START");

        PagedResult<RoleGroupManagementPojo> result = roleGroupService.findAllRoleGroups();

        logger.debug("findAllRoleGroups items={}", result.getItems());
        logger.debug("findAllRoleGroups totalCount={}", result.getTotalCount());
        logger.debug("findAllRoleGroups return={}", result);

        return ResponseEntity.ok(Response.success("All role groups fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/getAll")
    public ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> getAllRoleGroups() {
        logger.debug("getAllRoleGroups START");

        PagedResult<RoleGroupManagementPojo> result = roleGroupService.getAllRoleGroups();

        logger.debug("getAllRoleGroups items={}", result.getItems());
        logger.debug("getAllRoleGroups totalCount={}", result.getTotalCount());
        logger.debug("getAllRoleGroups return={}", result);

        return ResponseEntity.ok(Response.success("All role groups fetched successfully", result, HttpStatus.OK.value()));
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @GetMapping("/find")
    public ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> findRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("findRoleGroups START");
        logger.debug("findRoleGroups params={}", params);

        PagedResult<RoleGroupManagementPojo> result = roleGroupService.findRoleGroups(params);

        logger.debug("findRoleGroups items={}", result.getItems());
        logger.debug("findRoleGroups totalCount={}", result.getTotalCount());
        logger.debug("findRoleGroups return={}", result);

        return ResponseEntity.ok(Response.success("Filtered role groups fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> searchRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("searchRoleGroups START");
        logger.debug("searchRoleGroups params={}", params);

        PagedResult<RoleGroupManagementPojo> result = roleGroupService.searchRoleGroups(params);

        logger.debug("searchRoleGroups items={}", result.getItems());
        logger.debug("searchRoleGroups totalCount={}", result.getTotalCount());
        logger.debug("searchRoleGroups return={}", result);

        return ResponseEntity.ok(Response.success("Searched role groups fetched successfully", result, HttpStatus.OK.value()));
    }

    // ---------------- COUNT ----------------
    @GetMapping("/count")
    public ResponseEntity<Response<Long>> countRoleGroups(@RequestParam Map<String, Object> params) {
        logger.debug("countRoleGroups START");
        logger.debug("countRoleGroups params={}", params);

        long count = roleGroupService.countRoleGroups(params);

        logger.debug("countRoleGroups return={}", count);
        return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
    }
}
