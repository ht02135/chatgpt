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
        logger.debug("UserManagementController constructor called");
        logger.debug("UserManagementController userManagementService={}", userManagementService);
    }

    // =========================================================================
    // 5 CORE METHODS (PRIMARY)
    // =========================================================================

    // ------------------ CREATE ------------------
    @PostMapping("/create")
    public ResponseEntity<Response<UserManagementPojo>> create(@Valid @RequestBody(required = false) UserManagementPojo user) {
        logger.debug("create START");
        logger.debug("create user={}", user);

        if (user == null) {
            logger.debug("create: missing user payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing user payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        UserManagementPojo created = userManagementService.create(user);
        logger.debug("create return={}", created);
        return ResponseEntity.ok(Response.success("Created successfully", created, HttpStatus.OK.value()));
    }

    // ------------------ UPDATE ------------------
    @PutMapping("/update")
    public ResponseEntity<Response<UserManagementPojo>> update(
            @RequestParam(required = false) Long id,
            @Valid @RequestBody(required = false) UserManagementPojo user) {
        logger.debug("update START");
        logger.debug("update id={}", id);
        logger.debug("update user={}", user);

        if (id == null) {
            logger.debug("update: missing id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        if (user == null) {
            logger.debug("update: missing user payload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing user payload", null, HttpStatus.BAD_REQUEST.value()));
        }

        UserManagementPojo updated = userManagementService.update(id, user);
        logger.debug("update return={}", updated);
        return ResponseEntity.ok(Response.success("Updated successfully", updated, HttpStatus.OK.value()));
    }

    // ------------------ SEARCH ------------------
    @GetMapping("/search")
    public ResponseEntity<Response<PagedResult<UserManagementPojo>>> search(@RequestParam Map<String, String> params) {
        logger.debug("search START");
        logger.debug("search params={}", params);

        PagedResult<UserManagementPojo> result = userManagementService.search(params);
        logger.debug("search return={}", result);
        return ResponseEntity.ok(Response.success("Fetched successfully", result, HttpStatus.OK.value()));
    }

    // ------------------ GET BY ID ------------------
    @GetMapping("/get")
    public ResponseEntity<Response<UserManagementPojo>> get(@RequestParam(required = false) Long id) {
        logger.debug("get START");
        logger.debug("get id={}", id);

        if (id == null) {
            logger.debug("get: missing id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        UserManagementPojo user = userManagementService.get(id);
        logger.debug("get return={}", user);
        return ResponseEntity.ok(Response.success("Fetched successfully", user, HttpStatus.OK.value()));
    }

    // ------------------ DELETE ------------------
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> delete(@RequestParam(required = false) Long id) {
        logger.debug("delete START");
        logger.debug("delete id={}", id);

        if (id == null) {
            logger.debug("delete: missing id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.error("Missing id parameter", null, HttpStatus.BAD_REQUEST.value()));
        }

        userManagementService.delete(id);
        logger.debug("delete DONE");
        return ResponseEntity.ok(Response.success("Deleted successfully", null, HttpStatus.OK.value()));
    }

    // =========================================================================
    // ORIGINAL METHODS (USED BY CORE)
    // =========================================================================

}
