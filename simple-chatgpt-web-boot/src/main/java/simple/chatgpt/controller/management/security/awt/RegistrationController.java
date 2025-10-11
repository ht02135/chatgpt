package simple.chatgpt.controller.management.security.awt;

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

@RestController
@RequestMapping("/auth")
public class RegistrationController {

    private static final Logger logger = LogManager.getLogger(RegistrationController.class);

    private final UserManagementService userManagementService;
    private final RoleGroupManagementService roleGroupManagementService; // injected

    public RegistrationController(UserManagementService userManagementService,
                                  RoleGroupManagementService roleGroupManagementService) {
        logger.debug("RegistrationController constructor called");
        logger.debug("RegistrationController userManagementService={}", userManagementService);
        logger.debug("RegistrationController roleGroupManagementService={}", roleGroupManagementService);
        this.userManagementService = userManagementService;
        this.roleGroupManagementService = roleGroupManagementService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request) {
        logger.debug("registerUser called");
        logger.debug("registerUser request={}", request);

        // ===== Validate required input =====
        if (request.getUserName() == null || request.getPassword() == null) {
            logger.debug("registerUser invalid request: missing username or password");
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        // ===== Check for existing user =====
        UserManagementPojo existingUser = userManagementService.getUserByUserName(request.getUserName());
        logger.debug("registerUser existingUser={}", existingUser);

        if (existingUser != null) {
            logger.debug("registerUser username already exists={}", request.getUserName());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        // ===== Create new user POJO =====
        UserManagementPojo newUser = new UserManagementPojo();
        logger.debug("registerUser creating new UserManagementPojo");

        newUser.setUserName(request.getUserName());
        logger.debug("registerUser userName={}", request.getUserName());

        newUser.setPassword(request.getPassword()); // TODO: encode password
        logger.debug("registerUser password=[PROTECTED]");

        newUser.setFirstName(request.getFirstName());
        logger.debug("registerUser firstName={}", request.getFirstName());

        newUser.setLastName(request.getLastName());
        logger.debug("registerUser lastName={}", request.getLastName());

        newUser.setEmail(request.getEmail());
        logger.debug("registerUser email={}", request.getEmail());

        newUser.setAddressLine1(request.getAddressLine1());
        logger.debug("registerUser addressLine1={}", request.getAddressLine1());

        newUser.setAddressLine2(request.getAddressLine2());
        logger.debug("registerUser addressLine2={}", request.getAddressLine2());

        newUser.setCity(request.getCity());
        logger.debug("registerUser city={}", request.getCity());

        newUser.setState(request.getState());
        logger.debug("registerUser state={}", request.getState());

        newUser.setPostCode(request.getPostCode());
        logger.debug("registerUser postCode={}", request.getPostCode());

        newUser.setCountry(request.getCountry());
        logger.debug("registerUser country={}", request.getCountry());

        newUser.setActive(true);
        logger.debug("registerUser active={}", true);

        newUser.setLocked(false);
        logger.debug("registerUser locked={}", false);

        // ===== Assign default role group =====
        RoleGroupManagementPojo defaultRole =
                roleGroupManagementService.getRoleGroupByGroupName(RoleGroupManagementService.USER_ROLE_GROUP);
        logger.debug("registerUser defaultRole={}", defaultRole);

        if (defaultRole != null) {
            newUser.setRoleGroups(List.of(defaultRole));
            logger.debug("registerUser assigned defaultRole groupName={}", defaultRole.getGroupName());
        } else {
            logger.debug("registerUser no default role group found for name={}", RoleGroupManagementService.USER_ROLE_GROUP);
        }

        // ===== Persist user =====
        userManagementService.create(newUser);
        logger.debug("registerUser user created={}", newUser);

        // ===== Return success =====
        return ResponseEntity.ok("User registered successfully");
    }

}
