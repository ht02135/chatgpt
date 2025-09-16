package simple.chatgpt.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.pojo.User;
import simple.chatgpt.service.UserService;
import simple.chatgpt.util.Response;

/*
POST /chatgpt/api/users/add → add user
PUT /chatgpt/api/users/{id} → update user
DELETE /chatgpt/api/users/{id} → delete user
GET /chatgpt/api/users/{id} → get one
GET /chatgpt/api/users/all → get all
GET /chatgpt/api/users/test → test
///////////////////
Perfect — that proves everything is wired correctly:
✅ Your WAR context path = /chatgpt
✅ Your Servlet mapping = /api/*
✅ Your Controller mapping = /users
✅ Endpoint /test works as expected
*/

@RestController
@RequestMapping("/users")  // Make sure this matches your frontend URL
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    /*
    In Spring, beans like UserService, PropertyService, UserDao, etc., 
    are usually singletons. That means one instance is created at startup 
    and shared across all threads.
	Spring’s dependency injection happens once, during bean creation. After 
	that, Spring doesn’t reassign the field.
	//////////////
	even if i do @Autowired at here
	There’s no thread-safety issue.
	The reference won’t change after injection, so multiple threads won’t 
	see it “switching” to another instance.
	//////////////
	Recommendation (best practice in Spring Boot 3 / modern apps):
	1>Use constructor injection with final fields (your PropertyServiceImpl 
	is already a good example).
	2>Avoid field injection with @Autowired unless you’re wiring in test 
	code or legacy beans.
    */
    @Autowired
    private UserService userService;

    public UserController() {
        logger.info("UserController initialized!");
    }

    //------------------------------

    /*
    curl -X POST http://localhost:8080/chatgpt/api/users/add -H "Content-Type: application/json" -H "Accept: application/json" -d '{"name": "Test3", "email": "test@example3.com"}'
    */
    @PostMapping(value = "/add",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<User>> save(@RequestBody User user) {
        logger.debug("Received save request for user: {}", user.getName());

        boolean isUpdate = user.getId() > 0;
        User savedUser = userService.save(user);

        if (isUpdate) {
            logger.debug("Updated user: {}", savedUser);
            Response<User> response = Response.success("User updated successfully", savedUser, HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } else {
            logger.debug("Created new user: {}", savedUser);
            Response<User> response = Response.success("User created successfully", savedUser, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    @PostMapping(value = "/old/add",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<User>> addUser(@RequestBody User user) {
        return save(user);
    }

    //------------------------------

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<User>> update(@PathVariable int id, @RequestBody User user) {
        logger.debug("Received update request for user ID: {} with data: {}", id, user.getName());

        // Ensure the user ID matches the path variable
        user.setId(id);

        // Check if user exists first
        User existingUser = userService.get(id);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Response.error("User not found", null, HttpStatus.NOT_FOUND.value())
            );
        }

        User updatedUser = userService.save(user);
        logger.debug("Updated user: {}", updatedUser);

        Response<User> response = Response.success("User updated successfully", updatedUser, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    //------------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> delete(@PathVariable int id) {
        logger.debug("Received delete request for user ID: {}", id);
        userService.delete(id);
        return ResponseEntity.ok(
                Response.success("User deleted successfully", (Void) null, HttpStatus.OK.value())
        );
    }

    //------------------------------

    /*
    curl -X GET "http://localhost:8080/chatgpt/api/users/1" -H "Accept: application/json"
    */
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

    //------------------------------

    /*
    curl -X GET "http://localhost:8080/chatgpt/api/users/all" -H "Accept: application/json"
    */
    @GetMapping("/all")
    public ResponseEntity<Response<List<User>>> getAll() {
        logger.debug("Received get all users request");
        List<User> users = userService.getAll();
        logger.debug("users: {}", users);
        Response<List<User>> response = Response.success("Users retrieved successfully", users, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method=RequestMethod.GET, value = "/old/all")
    public ResponseEntity<Response<List<User>>> oldGetAll() {
        return getAll();
    }

    //------------------------------

    /*
    curl -X GET "http://localhost:8080/chatgpt/api/users/test" -H "Accept: application/json"
    */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.debug("calling test");
        return ResponseEntity.ok("API is working!");
    }
}