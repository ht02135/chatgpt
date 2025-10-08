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
import simple.chatgpt.util.ParamWrapper;
import simple.chatgpt.util.Response;
import simple.chatgpt.util.SafeConverter;

@RestController
@RequestMapping(value = "/management/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementController {
    private static final Logger logger = LogManager.getLogger(UserManagementController.class);

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
        logger.debug("UserManagementController constructor called");
        logger.debug("UserManagementController userManagementService={}", userManagementService);
    }

    // ------------------ LIST / SEARCH ------------------
    @GetMapping
    public ResponseEntity<Response<PagedResult<UserManagementPojo>>> searchUsers(
            @RequestParam Map<String, String> params) {
        logger.debug("searchUsers START");
        logger.debug("searchUsers raw params={}", params);

        // hung: DONT REMOVE THIS CODE
        int page = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "page", 0), 0); 
        int size = SafeConverter.toIntOrDefault(ParamWrapper.unwrap(params, "size", 20), 20);
        int offset = page * size;
        logger.debug("searchUsers page={}", page);
        logger.debug("searchUsers size={}", size);
        logger.debug("searchUsers offset={}", offset);

        params.put("offset", String.valueOf(offset));
        params.put("limit", String.valueOf(size));
        params.put("sortField", ParamWrapper.unwrap(params, "sortField", "id"));
        params.put("sortDirection", ParamWrapper.unwrap(params, "sortDirection", "asc"));

        PagedResult<UserManagementPojo> users = userManagementService.searchUsers(params);
        
        logger.debug("searchUsers result={}", users);
        return ResponseEntity.ok(Response.success("Fetched successfully", users, HttpStatus.OK.value()));
    }

    // ------------------ READ USER ------------------
    @GetMapping("/get")
    public ResponseEntity<Response<UserManagementPojo>> getUser(@RequestParam(required = false) Long id,
            @RequestParam(required = false) String userName, @RequestParam(required = false) String userKey) {
        logger.debug("getUser START");
        logger.debug("getUser id={}", id);
        logger.debug("getUser userName={}", userName);
        logger.debug("getUser userKey={}", userKey);

        UserManagementPojo user = null;

        if (id != null) {
            user = userManagementService.getUserById(id);
        } else if (userName != null) {
            user = userManagementService.getByUserName(userName);
        } else if (userKey != null) {
            user = userManagementService.getByUserKey(userKey);
        }

        if (user == null) {
            logger.debug("getUser: User not found");
            return ResponseEntity.ok(Response.error("User not found", null, HttpStatus.NOT_FOUND.value()));
        }

        logger.debug("getUser result={}", user);
        return ResponseEntity.ok(Response.success("Fetched successfully", user, HttpStatus.OK.value()));
    }

    // ------------------ CREATE USER ------------------
    @PostMapping("/create")
    public ResponseEntity<Response<UserManagementPojo>> createUser(@Valid @RequestBody UserManagementPojo user) {
        logger.debug("createUser START");
        logger.debug("createUser user={}", user);

        UserManagementPojo created = userManagementService.createUser(user);
        
        logger.debug("createUser created={}", created);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("User created successfully", created, HttpStatus.CREATED.value()));
    }

    // ------------------ UPDATE USER ------------------
    @PutMapping("/update")
    public ResponseEntity<Response<UserManagementPojo>> updateUser(
    	@RequestParam(required = false) Long id,
        @RequestParam(required = false) String userName, @RequestParam(required = false) String userKey,
        @Valid @RequestBody UserManagementPojo user) 
    {
        logger.debug("updateUser START");
        logger.debug("updateUser id={}", id);
        logger.debug("updateUser userName={}", userName);
        logger.debug("updateUser userKey={}", userKey);
        logger.debug("updateUser user={}", user);

        UserManagementPojo updated = null;

        if (id != null) {
            updated = userManagementService.updateUserById(id, user);
        } else if (userName != null) {
            updated = userManagementService.updateUserByUserName(userName, user);
        } else if (userKey != null) {
            updated = userManagementService.updateUserByUserKey(userKey, user);
        } else {
            logger.debug("updateUser: No key provided");
            return ResponseEntity.ok(Response.error("At least one key must be provided for update", null,
                    HttpStatus.BAD_REQUEST.value()));
        }

        logger.debug("updateUser updated={}", updated);
        return ResponseEntity.ok(Response.success("User updated successfully", updated, HttpStatus.OK.value()));
    }

    // ------------------ DELETE USER ------------------
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteUser(
    	@RequestParam(required = false) Long id,
        @RequestParam(required = false) String userName, 
        @RequestParam(required = false) String userKey) 
   {
        logger.debug("deleteUser START");
        logger.debug("deleteUser id={}", id);
        logger.debug("deleteUser userName={}", userName);
        logger.debug("deleteUser userKey={}", userKey);

        if (id != null) {
            userManagementService.deleteUserById(id);
        } else if (userName != null) {
            userManagementService.deleteUserByUserName(userName);
        } else if (userKey != null) {
            userManagementService.deleteUserByUserKey(userKey);
        } else {
            logger.debug("deleteUser: No key provided");
            return ResponseEntity.ok(Response.error("At least one key must be provided for delete", null,
                    HttpStatus.BAD_REQUEST.value()));
        }

        logger.debug("deleteUser DONE");
        return ResponseEntity.ok(Response.success("User deleted successfully", null, HttpStatus.OK.value()));
    }
}
