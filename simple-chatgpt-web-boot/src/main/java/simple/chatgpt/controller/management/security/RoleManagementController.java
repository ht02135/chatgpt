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

    // ---------------- CREATE ----------------
    @PostMapping("/insert")
    public ResponseEntity<Response<RoleManagementPojo>> insertRole(@RequestBody RoleManagementPojo role) {
        logger.debug("insertRole called role={}", role);
        Map<String, Object> params = new HashMap<>();
        params.put("role", role);

        RoleManagementPojo insertedRole = roleService.insertRole(params);
        logger.debug("insertRole result={}", insertedRole);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Role inserted successfully", insertedRole, HttpStatus.CREATED.value()));
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/update")
    public ResponseEntity<Response<RoleManagementPojo>> updateRole(@RequestBody RoleManagementPojo role,
                                                                   @RequestParam(required = false) Long roleId,
                                                                   @RequestParam(required = false) String roleName) {
        logger.debug("updateRole called role={}, roleId={}, roleName={}", role, roleId, roleName);
        Map<String, Object> params = new HashMap<>();
        params.put("role", role);
        if (roleId != null) params.put("roleId", roleId);
        if (roleName != null) params.put("roleName", roleName);

        RoleManagementPojo updatedRole = roleService.updateRole(params);
        logger.debug("updateRole result={}", updatedRole);

        return ResponseEntity.ok(Response.success("Role updated successfully", updatedRole, HttpStatus.OK.value()));
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/deleteById")
    public ResponseEntity<Response<Void>> deleteRoleById(@RequestParam Long roleId) {
        logger.debug("deleteRoleById called roleId={}", roleId);
        Map<String, Object> params = Map.of("roleId", roleId);

        roleService.deleteRoleById(params);
        logger.debug("deleteRoleById completed for roleId={}", roleId);

        return ResponseEntity.ok(Response.success("Role deleted by ID successfully", null, HttpStatus.OK.value()));
    }

    @DeleteMapping("/deleteByName")
    public ResponseEntity<Response<Void>> deleteRoleByName(@RequestParam String roleName) {
        logger.debug("deleteRoleByName called roleName={}", roleName);
        Map<String, Object> params = Map.of("roleName", roleName);

        roleService.deleteRoleByName(params);
        logger.debug("deleteRoleByName completed for roleName={}", roleName);

        return ResponseEntity.ok(Response.success("Role deleted by Name successfully", null, HttpStatus.OK.value()));
    }

    // ---------------- READ ----------------
    @GetMapping("/findById")
    public ResponseEntity<Response<RoleManagementPojo>> findRoleById(@RequestParam Long roleId) {
        logger.debug("findRoleById called roleId={}", roleId);
        Map<String, Object> params = Map.of("roleId", roleId);

        RoleManagementPojo role = roleService.findRoleById(params);
        logger.debug("findRoleById result={}", role);

        if (role == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Role not found by ID", null, HttpStatus.NOT_FOUND.value()));
        }
        return ResponseEntity.ok(Response.success("Role found by ID", role, HttpStatus.OK.value()));
    }

    @GetMapping("/findByName")
    public ResponseEntity<Response<RoleManagementPojo>> findRoleByName(@RequestParam String roleName) {
        logger.debug("findRoleByName called roleName={}", roleName);
        Map<String, Object> params = Map.of("roleName", roleName);

        RoleManagementPojo role = roleService.findRoleByName(params);
        logger.debug("findRoleByName result={}", role);

        if (role == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Role not found by Name", null, HttpStatus.NOT_FOUND.value()));
        }
        return ResponseEntity.ok(Response.success("Role found by Name", role, HttpStatus.OK.value()));
    }

    // ---------------- LIST / PAGINATION ----------------
    @GetMapping("/findAll")
    public ResponseEntity<Response<PagedResult<RoleManagementPojo>>> findAllRoles() {
        logger.debug("findAllRoles called");

        PagedResult<RoleManagementPojo> result = roleService.findAllRoles();
        logger.debug("findAllRoles returned {} items", result.getItems().size());

        return ResponseEntity.ok(Response.success("All roles fetched", result, HttpStatus.OK.value()));
    }

    @GetMapping("/getAll")
    public ResponseEntity<Response<PagedResult<RoleManagementPojo>>> getAllRoles() {
        logger.debug("getAllRoles called");

        PagedResult<RoleManagementPojo> result = roleService.getAllRoles();
        logger.debug("getAllRoles returned {} items", result.getItems().size());

        return ResponseEntity.ok(Response.success("All roles fetched", result, HttpStatus.OK.value()));
    }

    @GetMapping("/findRoles")
    public ResponseEntity<Response<PagedResult<RoleManagementPojo>>> findRoles(@RequestParam Map<String, Object> requestParams) {
        logger.debug("findRoles called requestParams={}", requestParams);
        PagedResult<RoleManagementPojo> result = roleService.findRoles(requestParams);
        logger.debug("findRoles returned {} items", result.getItems().size());

        return ResponseEntity.ok(Response.success("Roles found", result, HttpStatus.OK.value()));
    }

    @GetMapping("/searchRoles")
    public ResponseEntity<Response<PagedResult<RoleManagementPojo>>> searchRoles(@RequestParam Map<String, Object> requestParams) {
        logger.debug("searchRoles called requestParams={}", requestParams);
        PagedResult<RoleManagementPojo> result = roleService.searchRoles(requestParams);
        logger.debug("searchRoles returned {} items", result.getItems().size());

        return ResponseEntity.ok(Response.success("Roles searched", result, HttpStatus.OK.value()));
    }

    // ---------------- COUNT ----------------
    @GetMapping("/count")
    public ResponseEntity<Response<Long>> countRoles(@RequestParam Map<String, Object> requestParams) {
        logger.debug("countRoles called requestParams={}", requestParams);
        long count = roleService.countRoles(requestParams);
        logger.debug("countRoles result={}", count);

        return ResponseEntity.ok(Response.success("Roles count fetched", count, HttpStatus.OK.value()));
    }
}

/*
HUNG : DONT REMOVE
JS method name	JS API call	Controller API	Do we need to update JS API call?	Suggestion
saveRole	POST /api/management/roles/insert	/management/roles/insertRole	Yes	Update JS to call /insertRole instead of /insert
saveRole (edit)	PUT /api/management/roles/update?roleId=...	/management/roles/updateRole	Yes	Update JS to call /updateRole instead of /update
deleteRole	DELETE /api/management/roles/deleteById?roleId=...	/management/roles/deleteById	No	Already correct
loadRoles	GET /api/management/roles/searchRoles?...	/management/roles/searchRoles	No	Already correct
loadRoleById	GET /api/management/roles/findById?roleId=...	/management/roles/findById	No	Already correct
*/
