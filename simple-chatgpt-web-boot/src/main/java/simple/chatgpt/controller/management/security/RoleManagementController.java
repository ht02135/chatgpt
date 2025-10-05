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

import simple.chatgpt.pojo.management.security.RoleManagementPojo;
import simple.chatgpt.service.management.security.RoleManagementService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/roles", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleManagementController {

    private static final Logger logger = LogManager.getLogger(RoleManagementController.class);

    private final RoleManagementService roleService;

    public RoleManagementController(RoleManagementService roleService) {
        this.roleService = roleService;
        logger.debug("RoleManagementController constructor called, roleService={}", roleService);
    }

    // ➕ CREATE ROLE
    @PostMapping("/create")
    public ResponseEntity<Response<RoleManagementPojo>> createRole(@RequestBody RoleManagementPojo role) {
        logger.debug("createRole called");
        logger.debug("createRole role={}", role);

        Map<String, Object> params = new HashMap<>();
        params.put("role", role);
        RoleManagementPojo createdRole = roleService.createRole(params);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Role created successfully", createdRole, HttpStatus.CREATED.value()));
    }

    // 📖 GET ROLE BY ID
    @GetMapping("/get")
    public ResponseEntity<Response<RoleManagementPojo>> getRoleById(@RequestParam Long roleId) {
        logger.debug("getRoleById called, roleId={}", roleId);

        Map<String, Object> params = new HashMap<>();
        params.put("roleId", roleId);

        RoleManagementPojo role = roleService.getRole(params);
        if (role == null) {
            return ResponseEntity.ok(Response.error("Role not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("Role fetched successfully", role, HttpStatus.OK.value()));
    }

    // 📝 UPDATE ROLE
    @PutMapping("/update")
    public ResponseEntity<Response<RoleManagementPojo>> updateRole(
            @RequestParam Long roleId,
            @RequestBody RoleManagementPojo role
    ) {
        logger.debug("updateRole called, roleId={}", roleId);
        logger.debug("updateRole role={}", role);

        Map<String, Object> params = new HashMap<>();
        params.put("roleId", roleId);
        params.put("role", role);

        RoleManagementPojo updatedRole = roleService.updateRole(params);
        return ResponseEntity.ok(Response.success("Role updated successfully", updatedRole, HttpStatus.OK.value()));
    }

    // 🗑 DELETE ROLE
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteRole(@RequestParam Long roleId) {
        logger.debug("deleteRole called, roleId={}", roleId);

        Map<String, Object> params = new HashMap<>();
        params.put("roleId", roleId);

        roleService.deleteRole(params);
        return ResponseEntity.ok(Response.success("Role deleted successfully", null, HttpStatus.OK.value()));
    }

    // 🔍 SEARCH / PAGINATION
    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<RoleManagementPojo>>> searchRoles(
            @RequestParam Map<String, Object> requestParams
    ) {
        logger.debug("searchRoles called, requestParams={}", requestParams);

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

        PagedResult<RoleManagementPojo> pagedRoles = roleService.searchRoles(params);
        return ResponseEntity.ok(Response.success("Roles fetched successfully", pagedRoles, HttpStatus.OK.value()));
    }
}
