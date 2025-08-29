package simple.chatgpt.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import simple.chatgpt.pojo.User;
import simple.chatgpt.service.UserService;
import simple.chatgpt.util.Response;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    public UserController() {
        logger.info("UserController initialized!");
    }

    @PostMapping
    public ResponseEntity<Response<User>> save(@RequestBody User user) {
        logger.debug("Received save request for user: {}", user.getName());
        User savedUser = userService.save(user);
        logger.debug("Saved user: {}", savedUser);
        Response<User> response = Response.success("User added successfully", savedUser, HttpStatus.CREATED.value());
        logger.debug("Response: {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> delete(@PathVariable int id) {
        logger.debug("Received delete request for user ID: {}", id);
        userService.delete(id);
        return ResponseEntity.ok(
                Response.success("User deleted successfully", (Void) null, HttpStatus.OK.value())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<User>> get(@PathVariable int id) {
        logger.debug("Received get request for user ID: {}", id);
        User user = userService.get(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Response.error("User not found", null, HttpStatus.NOT_FOUND.value())
            );
        }
        return ResponseEntity.ok(
                Response.success("User retrieved successfully", user, HttpStatus.OK.value())
        );
    }

    @GetMapping
    public ResponseEntity<Response<List<User>>> getAll() {
        logger.debug("Received get all users request");
        List<User> users = userService.getAll();
        logger.debug("users: {}", users);
        Response<List<User>> response = Response.success("Users retrieved successfully", users, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.debug("calling test");
        return ResponseEntity.ok("API is working!");
    }
}
