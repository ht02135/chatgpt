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

    // ➕ CREATE ROLE GROUP
    @PostMapping("/create")
    public ResponseEntity<Response<RoleGroupManagementPojo>> createRoleGroup(@RequestBody RoleGroupManagementPojo group) {
        logger.debug("createRoleGroup called");
        logger.debug("createRoleGroup group={}", group);

        Map<String, Object> params = new HashMap<>();
        params.put("group", group);

        RoleGroupManagementPojo created = roleGroupService.createRoleGroup(params);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Role group created successfully", created, HttpStatus.CREATED.value()));
    }

    // 📖 GET ROLE GROUP BY ID
    @GetMapping("/get")
    public ResponseEntity<Response<RoleGroupManagementPojo>> getRoleGroupById(@RequestParam Long roleGroupId) {
        logger.debug("getRoleGroupById called, roleGroupId={}", roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("roleGroupId", roleGroupId);

        RoleGroupManagementPojo group = roleGroupService.getRoleGroup(params);
        if (group == null) {
            return ResponseEntity.ok(Response.error("Role group not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("Role group fetched successfully", group, HttpStatus.OK.value()));
    }

    // 📝 UPDATE ROLE GROUP
    @PutMapping("/update")
    public ResponseEntity<Response<RoleGroupManagementPojo>> updateRoleGroup(
            @RequestParam Long roleGroupId,
            @RequestBody RoleGroupManagementPojo group
    ) {
        logger.debug("updateRoleGroup called, roleGroupId={}", roleGroupId);
        logger.debug("updateRoleGroup group={}", group);

        Map<String, Object> params = new HashMap<>();
        params.put("roleGroupId", roleGroupId);
        params.put("group", group);

        RoleGroupManagementPojo updated = roleGroupService.updateRoleGroup(params);
        return ResponseEntity.ok(Response.success("Role group updated successfully", updated, HttpStatus.OK.value()));
    }

    // 🗑 DELETE ROLE GROUP
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteRoleGroup(@RequestParam Long roleGroupId) {
        logger.debug("deleteRoleGroup called, roleGroupId={}", roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("roleGroupId", roleGroupId);

        roleGroupService.deleteRoleGroup(params);
        return ResponseEntity.ok(Response.success("Role group deleted successfully", null, HttpStatus.OK.value()));
    }

    // 🔍 SEARCH / PAGINATION
    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<RoleGroupManagementPojo>>> searchRoleGroups(
            @RequestParam Map<String, Object> requestParams
    ) {
        logger.debug("searchRoleGroups called, requestParams={}", requestParams);

        Map<String, Object> params = new HashMap<>(requestParams);
        int page = requestParams.get("page") != null ? Integer.parseInt(requestParams.get("page").toString()) : 0;
        int size = requestParams.get("size") != null ? Integer.parseInt(requestParams.get("size").toString()) : 20;
        int offset = page * size;

        params.put("page", page);
        params.put("size", size);
        params.put("offset", offset);
        params.put("limit", size);
        params.put("sortField", requestParams.getOrDefault("sortField", "id"));
        params.put("sortDirection", requestParams.getOrDefault("sortDirection", "ASC"));

        PagedResult<RoleGroupManagementPojo> paged = roleGroupService.searchRoleGroups(params);
        return ResponseEntity.ok(Response.success("Role groups fetched successfully", paged, HttpStatus.OK.value()));
    }
}
