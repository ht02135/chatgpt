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

import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.service.management.security.RoleGroupRoleMappingService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.Response;
import simple.chatgpt.util.SafeConverter;

@RestController
@RequestMapping(value = "/management/rolegrouprolemappings", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleGroupRoleMappingController {

    private static final Logger logger = LogManager.getLogger(RoleGroupRoleMappingController.class);

    private final RoleGroupRoleMappingService mappingService;

    public RoleGroupRoleMappingController(RoleGroupRoleMappingService mappingService) {
        this.mappingService = mappingService;
        logger.debug("RoleGroupRoleMappingController constructor called, mappingService={}", mappingService);
    }

    // ==============================================================
    // ================ 5 CORE METHODS (on top) =====================
    // ==============================================================

    @PostMapping("/create")
    public ResponseEntity<Response<RoleGroupRoleMappingPojo>> create(
        @RequestBody(required = false) RoleGroupRoleMappingPojo mapping)
    {
        logger.debug("create called");
        logger.debug("create mapping={}", mapping);

        if (mapping == null) {
            logger.debug("create: missing mapping payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing mapping payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        RoleGroupRoleMappingPojo created = mappingService.create(mapping);

        logger.debug("create return={}", created);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Mapping created successfully", created, HttpStatus.CREATED.value()));
    }

    @PutMapping("/update")
    public ResponseEntity<Response<RoleGroupRoleMappingPojo>> update(
        @RequestParam(required = false) Long id,
        @RequestBody(required = false) RoleGroupRoleMappingPojo mapping)
    {
        logger.debug("update called");
        logger.debug("update id={}", id);
        logger.debug("update mapping={}", mapping);

        if (id == null) {
            logger.debug("update: missing mappingId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing mappingId parameter", null, HttpStatus.BAD_REQUEST.value()));
        }
        if (mapping == null) {
            logger.debug("update: missing mapping payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing mapping payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        RoleGroupRoleMappingPojo updated = mappingService.update(id, mapping);

        logger.debug("update return={}", updated);
        return ResponseEntity.ok(Response.success("Mapping updated successfully", updated, HttpStatus.OK.value()));
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> search(
        @RequestParam Map<String, String> params)
    {
        logger.debug("search called");
        logger.debug("search params={}", params);

        if (params == null || params.isEmpty()) {
            logger.debug("search: missing parameters");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing parameters", null, HttpStatus.BAD_REQUEST.value()));
        }

        // Default pagination
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

        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.search(params);

        logger.debug("search return totalCount={}", result.getTotalCount());
        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/get")
    public ResponseEntity<Response<RoleGroupRoleMappingPojo>> get(
        @RequestParam(required = false) Long id)
    {
        logger.debug("get called");
        logger.debug("get id={}", id);

        if (id == null) {
            logger.debug("get: missing mappingId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing mappingId parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        RoleGroupRoleMappingPojo result = mappingService.get(id);

        logger.debug("get return={}", result);
        return ResponseEntity.ok(Response.success("Mapping fetched successfully", result, HttpStatus.OK.value()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> delete(
        @RequestParam(required = false) Long id)
    {
        logger.debug("delete called");
        logger.debug("delete id={}", id);

        if (id == null) {
            logger.debug("delete: missing mappingId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing mappingId parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        mappingService.delete(id);

        logger.debug("delete successful for id={}", id);
        return ResponseEntity.ok(Response.success("Mapping deleted successfully", null, HttpStatus.OK.value()));
    }
    
}
