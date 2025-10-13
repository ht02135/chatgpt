package simple.chatgpt.controller.management;

import java.util.Map;

import javax.validation.Valid;

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

import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.service.management.UserManagementService;
import simple.chatgpt.service.management.security.UserManagementRoleGroupMappingService;
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementController {
    private static final Logger logger = LogManager.getLogger(UserManagementController.class);

    private final UserManagementService userManagementService;
    private final UserManagementRoleGroupMappingService mappingService;

    public UserManagementController(UserManagementService userManagementService,
                                    UserManagementRoleGroupMappingService mappingService) {
        this.userManagementService = userManagementService;
        this.mappingService = mappingService;

        logger.debug("UserManagementController constructor called");
        logger.debug("userManagementService={}", userManagementService);
        logger.debug("mappingService={}", mappingService);
    }

    // ------------------ CREATE ------------------
    @PostMapping("/create")
    public ResponseEntity<Response<UserManagementPojo>> create(@Valid @RequestBody UserManagementPojo user) {
        logger.debug("create START user={}", user);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing user payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        // 1️⃣ Create user
        UserManagementPojo created = userManagementService.create(user);
        logger.debug("create user created={}", created);

        // 2️⃣ Sync role-groups if provided
        if (user.getRoleGroups() != null && !user.getRoleGroups().isEmpty()) {
            mappingService.syncUserRoleGroups(created.getId(), user.getRoleGroups());
        }

        // 3️⃣ Populate role-groups for response
        created.setRoleGroups(mappingService.getUserRoleGroups(created.getId()));
        logger.debug("create return={}", created);

        return ResponseEntity.ok(Response.success("Created successfully", created, HttpStatus.OK.value()));
    }

    // ------------------ UPDATE ------------------
    @PutMapping("/update")
    public ResponseEntity<Response<UserManagementPojo>> update(@RequestParam Long id,
            @Valid @RequestBody UserManagementPojo user) {
        logger.debug("update START id={} user={}", id, user);

        if (id == null || user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id or user payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        // 1️⃣ Update user
        UserManagementPojo updated = userManagementService.update(id, user);
        logger.debug("update user updated={}", updated);

        // 2️⃣ Sync role-groups if provided
        if (user.getRoleGroups() != null) {
            mappingService.syncUserRoleGroups(id, user.getRoleGroups());
        }

        // 3️⃣ Populate role-groups for response
        updated.setRoleGroups(mappingService.getUserRoleGroups(id));
        logger.debug("update return={}", updated);

        return ResponseEntity.ok(Response.success("Updated successfully", updated, HttpStatus.OK.value()));
    }

    // ------------------ GET ------------------
    @GetMapping("/get")
    public ResponseEntity<Response<UserManagementPojo>> get(@RequestParam Long id) {
        logger.debug("get START id={}", id);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        UserManagementPojo user = userManagementService.get(id);

        if (user != null) {
            user.setRoleGroups(mappingService.getUserRoleGroups(id));
        }

        logger.debug("get return={}", user);
        return ResponseEntity.ok(Response.success("Fetched successfully", user, HttpStatus.OK.value()));
    }

    // ------------------ SEARCH ------------------
    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<UserManagementPojo>>> search(@RequestParam Map<String, String> params) {
        logger.debug("search START params={}", params);

        PagedResult<UserManagementPojo> result = userManagementService.search(params);

        // Populate role-groups for all users
        for (UserManagementPojo user : result.getItems()) {
            user.setRoleGroups(mappingService.getUserRoleGroups(user.getId()));
        }

        logger.debug("search return={}", result);
        return ResponseEntity.ok(Response.success("Fetched successfully", result, HttpStatus.OK.value()));
    }

    // ------------------ DELETE ------------------
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> delete(@RequestParam Long id) {
        logger.debug("delete START id={}", id);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        // 1️⃣ Delete user-role mappings first
        mappingService.deleteMappingsByUserId(id);

        // 2️⃣ Delete user
        userManagementService.delete(id);

        logger.debug("delete DONE id={}", id);
        return ResponseEntity.ok(Response.success("Deleted successfully", null, HttpStatus.OK.value()));
    }
}
