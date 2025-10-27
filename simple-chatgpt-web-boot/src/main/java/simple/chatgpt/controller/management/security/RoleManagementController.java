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
import simple.chatgpt.util.SafeConverter;

@RestController
@RequestMapping(value = "/management/roles", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleManagementController {

    private static final Logger logger = LogManager.getLogger(RoleManagementController.class);

    private final RoleManagementService roleService;

    public RoleManagementController(RoleManagementService roleService) {
        this.roleService = roleService;
        logger.debug("RoleManagementController constructor called, roleService={}", roleService);
    }
    
    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @PostMapping("/create")
    public ResponseEntity<Response<RoleManagementPojo>> create(
            @RequestBody(required = false) RoleManagementPojo role) {
        logger.debug("create called");
        logger.debug("create role={}", role);

        if (role == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing role payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        RoleManagementPojo created = roleService.create(role);
        logger.debug("create return={}", created);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Role created successfully", created, HttpStatus.CREATED.value()));
    }

    @PutMapping("/update")
    public ResponseEntity<Response<RoleManagementPojo>> update(
            @RequestParam(required = false) Long id,
            @RequestBody(required = false) RoleManagementPojo role) {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update role={}", role);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing roleId parameter", null, HttpStatus.BAD_REQUEST.value()));
        }
        if (role == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing role payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        RoleManagementPojo updated = roleService.update(id, role);
        logger.debug("update return={}", updated);
        return ResponseEntity.ok(Response.success("Role updated successfully", updated, HttpStatus.OK.value()));
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<RoleManagementPojo>>> search(
            @RequestParam Map<String, String> params) {
        logger.debug("search called");
        logger.debug("search params={}", params);

        if (params == null || params.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing parameters", null, HttpStatus.BAD_REQUEST.value()));
        }

        // Default pagination & sorting
        if (!params.containsKey("page")) params.put("page", "0");
        if (!params.containsKey("size")) params.put("size", "20");

        int page = SafeConverter.toIntOrDefault(params.get("page"), 0);
        int size = SafeConverter.toIntOrDefault(params.get("size"), 20);
        int offset = page * size;

        if (!params.containsKey("offset")) params.put("offset", String.valueOf(offset));
        if (!params.containsKey("limit")) params.put("limit", String.valueOf(size));

        if (!params.containsKey("sortField")) params.put("sortField", "id");
        if (!params.containsKey("sortDirection")) params.put("sortDirection", "ASC");
        params.put("sortDirection", params.get("sortDirection").toUpperCase());

        PagedResult<RoleManagementPojo> result = roleService.search(params);
        logger.debug("search return count={}", result.getItems().size());

        return ResponseEntity.ok(Response.success("Roles fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/get")
    public ResponseEntity<Response<RoleManagementPojo>> get(@RequestParam(required = false) Long id) {
        logger.debug("get called");
        logger.debug("get id={}", id);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing roleId parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        RoleManagementPojo result = roleService.get(id);
        logger.debug("get return={}", result);
        return ResponseEntity.ok(Response.success("Role fetched successfully", result, HttpStatus.OK.value()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> delete(@RequestParam(required = false) Long id) {
        logger.debug("delete called");
        logger.debug("delete id={}", id);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing roleId parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        roleService.delete(id);
        logger.debug("delete completed for id={}", id);
        return ResponseEntity.ok(Response.success("Role deleted successfully", null, HttpStatus.OK.value()));
    }

    // ==============================================================
    // ================ EXISTING METHODS (without URL mapping) ======
    // ==============================================================

}
