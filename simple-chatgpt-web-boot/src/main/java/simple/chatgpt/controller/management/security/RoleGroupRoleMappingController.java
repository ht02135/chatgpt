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

import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.service.management.security.RoleGroupRoleMappingService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/rolegrouprolemappings", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleGroupRoleMappingController implements RoleGroupRoleMappingControllerApi {

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

        return insertMapping(mapping);
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

        /*
        HUNG : REMOVE THE COMMENT
        for mapping, edit is delete previous mapping and then replace with new mapping
        */
        delete(id);
        return addRoleToGroupIfNotExists(mapping.getRoleGroupId(), mapping.getRoleId());
    }

    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> search(
        @RequestParam Map<String, Object> params)
    {
        logger.debug("search called");
        logger.debug("search params={}", params);

        if (params == null || params.isEmpty()) {
            logger.debug("search: missing parameters");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing parameters", null, HttpStatus.BAD_REQUEST.value()));
        }

        return findMappings(params);
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

		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		
		RoleGroupRoleMappingPojo result = mappingService.findById(params);

        logger.debug("get return={}", result);
        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
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

        return deleteMappingById(id);
    }
    
    // ---------------- CREATE ----------------
    @PostMapping("/add")
    public ResponseEntity<Response<RoleGroupRoleMappingPojo>> insertMapping(@RequestBody RoleGroupRoleMappingPojo mapping) {
        logger.debug("insertMapping START");
        logger.debug("insertMapping mapping={}", mapping);

        RoleGroupRoleMappingPojo result = mappingService.insertMapping(ParamWrapper.wrap("mapping", mapping));

        logger.debug("insertMapping return={}", result);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Mapping inserted successfully", result, HttpStatus.CREATED.value()));
    }

    @PostMapping("/addIfNotExists")
    public ResponseEntity<Response<RoleGroupRoleMappingPojo>> addRoleToGroupIfNotExists(
            @RequestParam Long roleGroupId,
            @RequestParam Long roleId
    ) {
        logger.debug("addRoleToGroupIfNotExists START");
        logger.debug("addRoleToGroupIfNotExists roleGroupId={}", roleGroupId);
        logger.debug("addRoleToGroupIfNotExists roleId={}", roleId);

        RoleGroupRoleMappingPojo result = mappingService.addRoleToGroupIfNotExists(
                ParamWrapper.wrap("roleGroupId", roleGroupId, "roleId", roleId)
        );

        logger.debug("addRoleToGroupIfNotExists return={}", result);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Role added to role group if not exists successfully", result, HttpStatus.CREATED.value()));
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/deleteById")
    public ResponseEntity<Response<Void>> deleteMappingById(@RequestParam Long id) {
        logger.debug("deleteMappingById START");
        logger.debug("deleteMappingById id={}", id);

        mappingService.deleteMappingById(ParamWrapper.wrap("id", id));

        logger.debug("deleteMappingById DONE");
        return ResponseEntity.ok(Response.success("Mapping deleted successfully", null, HttpStatus.OK.value()));
    }

    @DeleteMapping("/deleteByGroupAndRole")
    public ResponseEntity<Response<Void>> deleteMappingByGroupAndRole(
            @RequestParam Long roleGroupId,
            @RequestParam Long roleId
    ) {
        logger.debug("deleteMappingByGroupAndRole START");
        logger.debug("deleteMappingByGroupAndRole roleGroupId={}", roleGroupId);
        logger.debug("deleteMappingByGroupAndRole roleId={}", roleId);

        mappingService.deleteMappingByGroupAndRole(ParamWrapper.wrap("roleGroupId", roleGroupId, "roleId", roleId));

        logger.debug("deleteMappingByGroupAndRole DONE");
        return ResponseEntity.ok(Response.success("Mapping deleted successfully", null, HttpStatus.OK.value()));
    }

    // ---------------- READ ----------------
    @GetMapping("/listAll")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findAllMappings() {
        logger.debug("findAllMappings START");

        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.findAllMappings();

        logger.debug("findAllMappings return={}", result);
        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/listByRoleGroup")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findByRoleGroupId(@RequestParam Long roleGroupId) {
        logger.debug("findByRoleGroupId START");
        logger.debug("findByRoleGroupId roleGroupId={}", roleGroupId);

        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.findByRoleGroupId(ParamWrapper.wrap("roleGroupId", roleGroupId));

        logger.debug("findByRoleGroupId return={}", result);
        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/listByRole")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findByRoleId(@RequestParam Long roleId) {
        logger.debug("findByRoleId START");
        logger.debug("findByRoleId roleId={}", roleId);

        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.findByRoleId(ParamWrapper.wrap("roleId", roleId));

        logger.debug("findByRoleId return={}", result);
        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @GetMapping("/findMappings")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findMappings(@RequestParam Map<String, Object> params) {
        logger.debug("findMappings START");
        logger.debug("findMappings params={}", params);

        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.findMappings(params);

        logger.debug("findMappings return={}", result);
        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/searchMappings")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> searchMappings(@RequestParam Map<String, Object> params) {
        logger.debug("searchMappings START");
        logger.debug("searchMappings params={}", params);

        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.searchMappings(params);

        logger.debug("searchMappings return={}", result);
        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    // ---------------- COUNT ----------------
    @GetMapping("/countMappings")
    public ResponseEntity<Response<Long>> countMappings(@RequestParam Map<String, Object> params) {
        logger.debug("countMappings START");
        logger.debug("countMappings params={}", params);

        long count = mappingService.countMappings(params);

        logger.debug("countMappings return={}", count);
        return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
    }
}
