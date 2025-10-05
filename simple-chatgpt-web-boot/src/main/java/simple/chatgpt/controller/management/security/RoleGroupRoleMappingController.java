package simple.chatgpt.controller.management.security;

import java.util.HashMap;
import java.util.List;
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

    // ➕ ADD ROLE TO ROLE GROUP
    @PostMapping("/add")
    public ResponseEntity<Response<RoleGroupRoleMappingPojo>> addRoleToGroup(
            @RequestBody RoleGroupRoleMappingPojo mapping
    ) {
        logger.debug("addRoleToGroup called");
        logger.debug("addRoleToGroup mapping={}", mapping);

        Map<String, Object> params = new HashMap<>();
        params.put("mapping", mapping);

        RoleGroupRoleMappingPojo created = mappingService.addRoleToGroup(params);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Role added to role group successfully", created, HttpStatus.CREATED.value()));
    }

    // ➕ ADD ROLE TO GROUP IF NOT EXISTS
    @PostMapping("/addIfNotExists")
    public ResponseEntity<Response<RoleGroupRoleMappingPojo>> addRoleToGroupIfNotExists(
            @RequestParam Long roleGroupId,
            @RequestParam Long roleId
    ) {
        logger.debug("addRoleToGroupIfNotExists called, roleGroupId={}, roleId={}", roleGroupId, roleId);

        Map<String, Object> params = new HashMap<>();
        params.put("roleGroupId", roleGroupId);
        params.put("roleId", roleId);

        RoleGroupRoleMappingPojo created = mappingService.addRoleToGroupIfNotExists(params);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("Role added to role group if not exists successfully", created, HttpStatus.CREATED.value()));
    }

    // 🗑 REMOVE MAPPING
    @DeleteMapping("/remove")
    public ResponseEntity<Response<Void>> removeMapping(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long roleGroupId,
            @RequestParam(required = false) Long roleId
    ) {
        logger.debug("removeMapping called, id={}, roleGroupId={}, roleId={}", id, roleGroupId, roleId);

        Map<String, Object> params = new HashMap<>();
        if (id != null) params.put("id", id);
        if (roleGroupId != null) params.put("roleGroupId", roleGroupId);
        if (roleId != null) params.put("roleId", roleId);

        mappingService.removeMapping(params);

        return ResponseEntity.ok(Response.success("Mapping removed successfully", null, HttpStatus.OK.value()));
    }

    // 🔍 LIST ALL MAPPINGS
    @GetMapping("/list")
    public ResponseEntity<Response<List<RoleGroupRoleMappingPojo>>> listAll(
            @RequestParam Map<String, Object> requestParams
    ) {
        logger.debug("listAll called, requestParams={}", requestParams);

        Map<String, Object> params = new HashMap<>(requestParams);
        List<RoleGroupRoleMappingPojo> mappings = mappingService.findAllMappings(params);

        return ResponseEntity.ok(Response.success("Mappings fetched successfully", mappings, HttpStatus.OK.value()));
    }

    // 🔍 LIST BY ROLE GROUP
    @GetMapping("/listByRoleGroup")
    public ResponseEntity<Response<List<RoleGroupRoleMappingPojo>>> listByRoleGroup(@RequestParam Long roleGroupId) {
        logger.debug("listByRoleGroup called, roleGroupId={}", roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("roleGroupId", roleGroupId);

        List<RoleGroupRoleMappingPojo> mappings = mappingService.findByRoleGroup(params);

        return ResponseEntity.ok(Response.success("Mappings for role group fetched successfully", mappings, HttpStatus.OK.value()));
    }

    // 🔍 LIST BY ROLE
    @GetMapping("/listByRole")
    public ResponseEntity<Response<List<RoleGroupRoleMappingPojo>>> listByRole(@RequestParam Long roleId) {
        logger.debug("listByRole called, roleId={}", roleId);

        Map<String, Object> params = new HashMap<>();
        params.put("roleId", roleId);

        List<RoleGroupRoleMappingPojo> mappings = mappingService.findByRole(params);

        return ResponseEntity.ok(Response.success("Mappings for role fetched successfully", mappings, HttpStatus.OK.value()));
    }
}
