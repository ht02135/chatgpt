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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.service.management.security.UserManagementRoleGroupMappingService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;
import simple.chatgpt.util.SafeConverter;

@RestController
@RequestMapping(value = "/management/userrolegroups", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementRoleGroupMappingController {

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

        UserManagementRoleGroupMappingPojo created = mappingService.create(mapping);

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

        UserManagementRoleGroupMappingPojo updated = mappingService.update(id, mapping);

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

        UserManagementRoleGroupMappingPojo result = mappingService.get(id);
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
    
}
