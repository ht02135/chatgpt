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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.pojo.management.security.UserManagementRoleGroupMappingPojo;
import simple.chatgpt.service.management.security.UserManagementRoleGroupMappingService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/userrolegroups", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRoleGroupManagementController {

    private static final Logger logger = LogManager.getLogger(UserRoleGroupManagementController.class);

    private final UserManagementRoleGroupMappingService mappingService;

    public UserRoleGroupManagementController(UserManagementRoleGroupMappingService mappingService) {
        this.mappingService = mappingService;
        logger.debug("UserRoleGroupManagementController constructor called, mappingService={}", mappingService);
    }

    // ➕ ADD USER TO ROLE GROUP
    @PostMapping("/add")
    public ResponseEntity<Response<UserManagementRoleGroupMappingPojo>> addUserToRoleGroup(
            @RequestParam Long userId,
            @RequestParam Long roleGroupId
    ) {
        logger.debug("addUserToRoleGroup called, userId={}, roleGroupId={}", userId, roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("roleGroupId", roleGroupId);

        UserManagementRoleGroupMappingPojo mapping = mappingService.addUserToRoleGroup(params);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("User added to role group successfully", mapping, HttpStatus.CREATED.value()));
    }

    // 🗑 REMOVE MAPPING BY ID
    @DeleteMapping("/remove")
    public ResponseEntity<Response<Void>> removeMappingById(@RequestParam Long mappingId) {
        logger.debug("removeMappingById called, mappingId={}", mappingId);

        Map<String, Object> params = new HashMap<>();
        params.put("id", mappingId);

        mappingService.removeMappingById(params);
        return ResponseEntity.ok(Response.success("Mapping removed successfully", null, HttpStatus.OK.value()));
    }

    // 🗑 REMOVE MAPPING BY USER AND GROUP
    @DeleteMapping("/removeByUserAndGroup")
    public ResponseEntity<Response<Void>> removeMappingByUserAndGroup(
            @RequestParam Long userId,
            @RequestParam Long roleGroupId
    ) {
        logger.debug("removeMappingByUserAndGroup called, userId={}, roleGroupId={}", userId, roleGroupId);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("roleGroupId", roleGroupId);

        mappingService.removeMappingByUserAndGroup(params);
        return ResponseEntity.ok(Response.success("Mapping removed successfully", null, HttpStatus.OK.value()));
    }

    // 🔍 LIST ALL MAPPINGS (returns PagedResult)
    @GetMapping("/list")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        logger.debug("listAll called, page={}, size={}", page, size);

        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.findAll();
        return ResponseEntity.ok(Response.success("Mappings fetched successfully", paged, HttpStatus.OK.value()));
    }

    // 🔍 LIST BY USER ID (returns PagedResult)
    @GetMapping("/listByUser")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> listByUserId(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        logger.debug("listByUserId called, userId={}, page={}, size={}", userId, page, size);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("page", page);
        params.put("size", size);

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.findByUserId(params);
        return ResponseEntity.ok(Response.success("Mappings for user fetched successfully", paged, HttpStatus.OK.value()));
    }

    // 🔍 LIST BY ROLE GROUP ID (returns PagedResult)
    @GetMapping("/listByRoleGroup")
    public ResponseEntity<Response<PagedResult<UserManagementRoleGroupMappingPojo>>> listByRoleGroupId(
            @RequestParam Long roleGroupId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        logger.debug("listByRoleGroupId called, roleGroupId={}, page={}, size={}", roleGroupId, page, size);

        Map<String, Object> params = new HashMap<>();
        params.put("roleGroupId", roleGroupId);
        params.put("page", page);
        params.put("size", size);

        PagedResult<UserManagementRoleGroupMappingPojo> paged = mappingService.findByRoleGroupId(params);
        return ResponseEntity.ok(Response.success("Mappings for role group fetched successfully", paged, HttpStatus.OK.value()));
    }
}
