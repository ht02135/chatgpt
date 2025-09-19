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
import simple.chatgpt.util.PagedResult;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping(value = "/management/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementController {
    private static final Logger logger = LogManager.getLogger(UserManagementController.class);

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    // 🔎 LIST / SEARCH
    /*
    1>Simple pagination request:
	GET /users?page=0&size=20
	2>With filters:
	GET /users?firstName=John&city=New+York&page=1&size=10
	3>With sorting:
	GET /users?sortField=last_name&sortDirection=desc
    */
    @GetMapping
    public ResponseEntity<Response<PagedResult<UserManagementPojo>>> searchUsers(
            @RequestParam Map<String, String> params
    ) {
        logger.debug("searchUsers called with params={}", params);

        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "20"));
        int offset = page * size;

        params.put("offset", String.valueOf(offset));
        params.put("limit", String.valueOf(size));
        params.put("sortField", params.getOrDefault("sortField", "id"));
        params.put("sortDirection", params.getOrDefault("sortDirection", "asc"));

        PagedResult<UserManagementPojo> users = userManagementService.searchUsers(params);
        return ResponseEntity.ok(Response.success("Fetched successfully", users, HttpStatus.OK.value()));
    }

    // 📖 READ (Flexible key)
    @GetMapping("/get")
    public ResponseEntity<Response<UserManagementPojo>> getUser(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String userKey
    ) {
        logger.debug("getUser called with id={}, userName={}, userKey={}", id, userName, userKey);

        UserManagementPojo user = null;

        if (id != null) {
            user = userManagementService.getUserById(id);
        } else if (userName != null) {
            user = userManagementService.getByUserName(userName);
        } else if (userKey != null) {
            user = userManagementService.getByUserKey(userKey);
        }

        if (user == null) {
            return ResponseEntity.ok(Response.error("User not found", null, HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(Response.success("Fetched successfully", user, HttpStatus.OK.value()));
    }

    // ➕ CREATE
    @PostMapping("/create")
    public ResponseEntity<Response<UserManagementPojo>> createUser(
            @Valid @RequestBody UserManagementPojo user) {

        logger.debug("#############");
        logger.debug("createUser called with user={}", user);

        UserManagementPojo created = userManagementService.createUser(user);
        logger.debug("createUser created={}", created);
        logger.debug("#############");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success("User created successfully", created, HttpStatus.CREATED.value()));
    }

    // ✏️ UPDATE (Flexible key)
    @PutMapping("/update")
    public ResponseEntity<Response<UserManagementPojo>> updateUser(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String userKey,
            @Valid @RequestBody UserManagementPojo user
    ) {
        logger.debug("updateUser called with id={}, userName={}, userKey={}, user={}", id, userName, userKey, user);

        UserManagementPojo updated = null;

        logger.debug("#############");
        if (id != null) {
            updated = userManagementService.updateUserById(id, user);
        } else if (userName != null) {
            updated = userManagementService.updateUserByUserName(userName, user);
        } else if (userKey != null) {
            updated = userManagementService.updateUserByUserKey(userKey, user);
        } else {
            return ResponseEntity.ok(Response.error("At least one key must be provided for update", null, HttpStatus.BAD_REQUEST.value()));
        }
        logger.debug("updateUser updated={}", updated);
        logger.debug("#############");

        return ResponseEntity.ok(Response.success("User updated successfully", updated, HttpStatus.OK.value()));
    }

    // 🗑 DELETE (Flexible key)
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteUser(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String userKey
    ) {
        logger.debug("deleteUser called with id={}, userName={}, userKey={}", id, userName, userKey);

        if (id != null) {
            userManagementService.deleteUserById(id);
        } else if (userName != null) {
            userManagementService.deleteUserByUserName(userName);
        } else if (userKey != null) {
            userManagementService.deleteUserByUserKey(userKey);
        } else {
            return ResponseEntity.ok(Response.error("At least one key must be provided for delete", null, HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity.ok(Response.success("User deleted successfully", null, HttpStatus.OK.value()));
    }
}
