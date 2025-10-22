package simple.chatgpt.controller.management.awt;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.pojo.management.UserManagementPojo;
import simple.chatgpt.pojo.management.security.RoleGroupManagementPojo;
import simple.chatgpt.service.management.UserManagementService;
import simple.chatgpt.service.management.security.RoleGroupManagementService;
import simple.chatgpt.util.Response;

@RestController
@RequestMapping("/management/auth")
public class RegistrationController {

    private static final Logger logger = LogManager.getLogger(RegistrationController.class);

    // Constants for default messages
    private static final String USERNAME_REQUIRED = "Username and password are required";
    private static final String USERNAME_EXISTS = "Username already exists";
    private static final String REGISTRATION_SUCCESS = "User registered successfully";

    private final UserManagementService userManagementService;
    private final RoleGroupManagementService roleGroupManagementService;

    public RegistrationController(UserManagementService userManagementService,
                                  RoleGroupManagementService roleGroupManagementService) {
        logger.debug("RegistrationController constructor called");
        this.userManagementService = userManagementService;
        this.roleGroupManagementService = roleGroupManagementService;
    }

    @PostMapping("/register")
    public ResponseEntity<Response<UserManagementPojo>> registerUser(@RequestBody UserRegistrationRequest request) {
        logger.debug("registerUser called");
        logger.debug("registerUser request={}", request);

        // ===== Validate required input =====
        if (request.getUserName() == null || request.getPassword() == null) {
            logger.debug("registerUser invalid request: missing username or password");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Response.error(USERNAME_REQUIRED, null, HttpStatus.BAD_REQUEST.value()));
        }

        // ===== Check for existing user =====
        UserManagementPojo existingUser = userManagementService.getUserByUserName(request.getUserName());
        logger.debug("registerUser existingUser={}", existingUser);

        if (existingUser != null) {
            logger.debug("registerUser username already exists={}", request.getUserName());
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Response.error(USERNAME_EXISTS, null, HttpStatus.CONFLICT.value()));
        }

        // ===== Create new user POJO =====
        UserManagementPojo newUser = new UserManagementPojo();
        newUser.setUserName(request.getUserName());
        newUser.setPassword(request.getPassword()); // TODO: encode password
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setEmail(request.getEmail());
        newUser.setAddressLine1(request.getAddressLine1());
        newUser.setAddressLine2(request.getAddressLine2());
        newUser.setCity(request.getCity());
        newUser.setState(request.getState());
        newUser.setPostCode(request.getPostCode());
        newUser.setCountry(request.getCountry());
        newUser.setActive(true);
        newUser.setLocked(false);

        // ===== Assign default role group =====
        RoleGroupManagementPojo defaultRole =
                roleGroupManagementService.getRoleGroupByGroupName(RoleGroupManagementService.USER_ROLE_GROUP);

        if (defaultRole != null) {
            newUser.setRoleGroups(List.of(defaultRole));
        }

        // ===== Persist user =====
        userManagementService.create(newUser);
        logger.debug("registerUser user created={}", newUser);

        // ===== Return success with JSON =====
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success(REGISTRATION_SUCCESS, newUser, HttpStatus.CREATED.value()));
    }
}
