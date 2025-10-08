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
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/roles", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleManagementController implements RoleManagementControllerApi {

    private static final Logger logger = LogManager.getLogger(RoleManagementController.class);

    private final RoleManagementService roleService;

    public RoleManagementController(RoleManagementService roleService) {
        this.roleService = roleService;
        logger.debug("RoleManagementController constructor called, roleService={}", roleService);
    }

    // ---------------- CREATE ----------------
    @PostMapping("/insert")
    public ResponseEntity<Response<RoleManagementPojo>> insertRole(@RequestBody RoleManagementPojo role) {
        logger.debug("insertRole START");
        logger.debug("insertRole role={}", role);

        Map<String, Object> params = new HashMap<>();
        params.put("role", role);

        RoleManagementPojo insertedRole = roleService.insertRole(params);

        logger.debug("insertRole return={}", insertedRole);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Role inserted successfully", insertedRole, HttpStatus.CREATED.value()));
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/update")
    public ResponseEntity<Response<RoleManagementPojo>> updateRole(@RequestBody RoleManagementPojo role,
                                                                   @RequestParam(required = false) Long roleId,
                                                                   @RequestParam(required = false) String roleName) {
        logger.debug("updateRole START");
        logger.debug("updateRole role={}", role);
        logger.debug("updateRole roleId={}", roleId);
        logger.debug("updateRole roleName={}", roleName);

        Map<String, Object> params = new HashMap<>();
        params.put("role", role);
        if (roleId != null) params.put("roleId", roleId);
        if (roleName != null) params.put("roleName", roleName);

        RoleManagementPojo updatedRole = roleService.updateRole(params);

        logger.debug("updateRole updatedRole={}", updatedRole);
        return ResponseEntity.ok(Response.success("Role updated successfully", updatedRole, HttpStatus.OK.value()));
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/deleteById")
    public ResponseEntity<Response<Void>> deleteRoleById(@RequestParam Long roleId) {
        logger.debug("deleteRoleById START");
        logger.debug("deleteRoleById roleId={}", roleId);

        Map<String, Object> params = ParamWrapper.wrap("roleId", roleId);
        roleService.deleteRoleById(params);

        logger.debug("deleteRoleById DONE");
        return ResponseEntity.ok(Response.success("Role deleted by ID successfully", null, HttpStatus.OK.value()));
    }

    // ---------------- READ ----------------
    @GetMapping("/findById")
    public ResponseEntity<Response<RoleManagementPojo>> findRoleById(@RequestParam Long roleId) {
        logger.debug("findRoleById START");
        logger.debug("findRoleById roleId={}", roleId);

        Map<String, Object> params = ParamWrapper.wrap("roleId", roleId);
        RoleManagementPojo role = roleService.findRoleById(params);

        logger.debug("findRoleById role={}", role);
        if (role == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.error("Role not found by ID", null, HttpStatus.NOT_FOUND.value()));
        }

        logger.debug("findRoleById return={}", role);
        return ResponseEntity.ok(Response.success("Role found by ID", role, HttpStatus.OK.value()));
    }

    // ---------------- LIST / PAGINATION ----------------
    @GetMapping("/findAll")
    public ResponseEntity<Response<PagedResult<RoleManagementPojo>>> findAllRoles() {
        logger.debug("findAllRoles START");

        PagedResult<RoleManagementPojo> result = roleService.findAllRoles();

        logger.debug("findAllRoles return={}", result);
        return ResponseEntity.ok(Response.success("All roles fetched", result, HttpStatus.OK.value()));
    }

    @GetMapping("/getAll")
    public ResponseEntity<Response<PagedResult<RoleManagementPojo>>> getAllRoles() {
        logger.debug("getAllRoles START");

        PagedResult<RoleManagementPojo> result = roleService.getAllRoles();

        logger.debug("getAllRoles return={}", result);
        return ResponseEntity.ok(Response.success("All roles fetched", result, HttpStatus.OK.value()));
    }

    @GetMapping("/findRoles")
    public ResponseEntity<Response<PagedResult<RoleManagementPojo>>> findRoles(@RequestParam Map<String, Object> requestParams) {
        logger.debug("findRoles START");
        logger.debug("findRoles requestParams={}", requestParams);

        PagedResult<RoleManagementPojo> result = roleService.findRoles(requestParams);

        logger.debug("findRoles return={}", result);
        return ResponseEntity.ok(Response.success("Roles found", result, HttpStatus.OK.value()));
    }

    @GetMapping("/searchRoles")
    public ResponseEntity<Response<PagedResult<RoleManagementPojo>>> searchRoles(@RequestParam Map<String, Object> requestParams) {
        logger.debug("searchRoles START");
        logger.debug("searchRoles requestParams={}", requestParams);

        PagedResult<RoleManagementPojo> result = roleService.searchRoles(requestParams);

        logger.debug("searchRoles return={}", result);
        return ResponseEntity.ok(Response.success("Roles searched", result, HttpStatus.OK.value()));
    }

    // ---------------- COUNT ----------------
    @GetMapping("/count")
    public ResponseEntity<Response<Long>> countRoles(@RequestParam Map<String, Object> requestParams) {
        logger.debug("countRoles START");
        logger.debug("countRoles requestParams={}", requestParams);

        long count = roleService.countRoles(requestParams);

        logger.debug("countRoles return={}", count);
        return ResponseEntity.ok(Response.success("Roles count fetched", count, HttpStatus.OK.value()));
    }
}
