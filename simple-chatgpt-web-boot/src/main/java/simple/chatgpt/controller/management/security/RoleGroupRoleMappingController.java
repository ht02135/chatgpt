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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.pojo.management.security.RoleGroupRoleMappingPojo;
import simple.chatgpt.service.management.security.RoleGroupRoleMappingService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/rolegrouprolemappings", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleGroupRoleMappingController {

    private static final Logger logger = LogManager.getLogger(RoleGroupRoleMappingController.class);

    private final RoleGroupRoleMappingService mappingService;

    public RoleGroupRoleMappingController(RoleGroupRoleMappingService mappingService) {
        this.mappingService = mappingService;
        logger.debug("RoleGroupRoleMappingController constructor called, mappingService={}", mappingService);
    }

    // ---------------- CREATE ----------------
    @PostMapping("/add")
    public ResponseEntity<Response<RoleGroupRoleMappingPojo>> insertMapping(@RequestBody RoleGroupRoleMappingPojo mapping) {
        logger.debug("insertMapping called mapping={}", mapping);
        RoleGroupRoleMappingPojo result = mappingService.insertMapping(Map.of("mapping", mapping));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Mapping inserted successfully", result, HttpStatus.CREATED.value()));
    }

    @PostMapping("/addIfNotExists")
    public ResponseEntity<Response<RoleGroupRoleMappingPojo>> addRoleToGroupIfNotExists(
            @RequestParam Long roleGroupId,
            @RequestParam Long roleId
    ) {
        logger.debug("addRoleToGroupIfNotExists called, roleGroupId={}, roleId={}", roleGroupId, roleId);
        RoleGroupRoleMappingPojo result = mappingService.addRoleToGroupIfNotExists(
                Map.of("roleGroupId", roleGroupId, "roleId", roleId)
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Role added to role group if not exists successfully", result, HttpStatus.CREATED.value()));
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/deleteById")
    public ResponseEntity<Response<Void>> deleteMappingById(@RequestParam Long id) {
        logger.debug("deleteMappingById called, id={}", id);
        mappingService.deleteMappingById(Map.of("id", id));
        return ResponseEntity.ok(Response.success("Mapping deleted successfully", null, HttpStatus.OK.value()));
    }

    @DeleteMapping("/deleteByGroupAndRole")
    public ResponseEntity<Response<Void>> deleteMappingByGroupAndRole(
            @RequestParam Long roleGroupId,
            @RequestParam Long roleId
    ) {
        logger.debug("deleteMappingByGroupAndRole called, roleGroupId={}, roleId={}", roleGroupId, roleId);
        mappingService.deleteMappingByGroupAndRole(Map.of("roleGroupId", roleGroupId, "roleId", roleId));
        return ResponseEntity.ok(Response.success("Mapping deleted successfully", null, HttpStatus.OK.value()));
    }

    // ---------------- READ ----------------
    @GetMapping("/listAll")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findAllMappings() {
        logger.debug("findAllMappings called");
        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.findAllMappings();
        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/listByRoleGroup")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findByRoleGroupId(@RequestParam Long roleGroupId) {
        logger.debug("findByRoleGroupId called, roleGroupId={}", roleGroupId);
        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.findByRoleGroupId(Map.of("roleGroupId", roleGroupId));
        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/listByRole")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findByRoleId(@RequestParam Long roleId) {
        logger.debug("findByRoleId called, roleId={}", roleId);
        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.findByRoleId(Map.of("roleId", roleId));
        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    // ---------------- SEARCH / PAGINATION ----------------
    @GetMapping("/findMappings")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> findMappings(@RequestParam Map<String, Object> params) {
        logger.debug("findMappings called, params={}", params);
        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.findMappings(params);
        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    @GetMapping("/searchMappings")
    public ResponseEntity<Response<PagedResult<RoleGroupRoleMappingPojo>>> searchMappings(@RequestParam Map<String, Object> params) {
        logger.debug("searchMappings called, params={}", params);
        PagedResult<RoleGroupRoleMappingPojo> result = mappingService.searchMappings(params);
        return ResponseEntity.ok(Response.success("Mappings fetched successfully", result, HttpStatus.OK.value()));
    }

    // ---------------- COUNT ----------------
    @GetMapping("/countMappings")
    public ResponseEntity<Response<Long>> countMappings(@RequestParam Map<String, Object> params) {
        logger.debug("countMappings called, params={}", params);
        long count = mappingService.countMappings(params);
        return ResponseEntity.ok(Response.success("Count fetched successfully", count, HttpStatus.OK.value()));
    }
}

/*
HUNG : DONT REMOVE
Here’s the table for your roleGroupRole.js mapping JS methods to controller APIs, in the format you requested:

js method name	js api call	controller api	do we need to update js api call	suggestion
loadRoleGroupRoles	${API_ROLE_GROUP_ROLE}/searchMappings?${qs}	/management/rolegrouprolemappings/searchMappings	No	Already correct
addRoleToGroup	N/A	N/A	N/A	Navigation to addRoleGroupRole.jsp, no API call
editRoleGroupRole	N/A	N/A	N/A	Navigation to editRoleGroupRole.jsp, no API call
deleteRoleGroupRole	${API_ROLE_GROUP_ROLE}/deleteById?id=${id}	/management/rolegrouprolemappings/deleteById	No	Already correct
saveRoleGroupRole	${API_ROLE_GROUP_ROLE}/add (create)
${API_ROLE_GROUP_ROLE}/addIfNotExists?roleGroupId=...&roleId=... (edit)	/management/rolegrouprolemappings/add
/management/rolegrouprolemappings/addIfNotExists	No	Already correct
loadRoleGroupRoleById	${API_ROLE_GROUP_ROLE}/listAll	/management/rolegrouprolemappings/listAll	No	Already correct
searchRoleGroupRoles	${API_ROLE_GROUP_ROLE}/searchMappings?${qs}	/management/rolegrouprolemappings/searchMappings	No	Already
*/