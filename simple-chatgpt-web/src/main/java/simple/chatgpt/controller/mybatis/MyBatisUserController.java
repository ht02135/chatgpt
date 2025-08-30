package simple.chatgpt.controller.mybatis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import simple.chatgpt.pojo.User;
import simple.chatgpt.service.mybatis.MyBatisUserService;
import simple.chatgpt.util.Response;

import java.util.List;

/*
MyBatis version of UserController
POST /chatgpt/api/mybatis/users/add → add user
PUT /chatgpt/api/mybatis/users/{id} → update user
DELETE /chatgpt/api/mybatis/users/{id} → delete user
GET /chatgpt/api/mybatis/users/{id} → get one
GET /chatgpt/api/mybatis/users/all → get all
GET /chatgpt/api/mybatis/users/test → test
*/

@RestController
@RequestMapping("/mybatis/users")  // Make sure this matches your frontend URL
public class MyBatisUserController {
    private static final Logger logger = LogManager.getLogger(MyBatisUserController.class);

    @Autowired
    private MyBatisUserService myBatisUserService;

    public MyBatisUserController() {
        logger.info("MyBatisUserController initialized!");
    }

    //------------------------------

    /*
    curl -X POST http://localhost:8080/chatgpt/api/mybatis/users/add -H "Content-Type: application/json" -H "Accept: application/json" -d '{"name": "Test3", "email": "test@example3.com"}'
    */
    @PostMapping(value = "/add",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<User>> save(@RequestBody User user) {
        logger.debug("MyBatis - Received save request for user: {}", user.getName());

        boolean isUpdate = user.getId() > 0;
        User savedUser = myBatisUserService.save(user);

        if (isUpdate) {
            logger.debug("MyBatis - Updated user: {}", savedUser);
            Response<User> response = Response.success("User updated successfully", savedUser, HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } else {
            logger.debug("MyBatis - Created new user: {}", savedUser);
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
        logger.debug("MyBatis - Received update request for user ID: {} with data: {}", id, user.getName());

        // Ensure the user ID matches the path variable
        user.setId(id);

        // Check if user exists first
        User existingUser = myBatisUserService.get(id);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Response.error("User not found", null, HttpStatus.NOT_FOUND.value())
            );
        }

        User updatedUser = myBatisUserService.save(user);
        logger.debug("MyBatis - Updated user: {}", updatedUser);

        Response<User> response = Response.success("User updated successfully", updatedUser, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    //------------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> delete(@PathVariable int id) {
        logger.debug("MyBatis - Received delete request for user ID: {}", id);
        myBatisUserService.delete(id);
        return ResponseEntity.ok(
                Response.success("User deleted successfully", (Void) null, HttpStatus.OK.value())
        );
    }

    //------------------------------

    /*
    curl -X GET "http://localhost:8080/chatgpt/api/mybatis/users/1" -H "Accept: application/json"
    */
    @GetMapping("/{id}")
    public ResponseEntity<Response<User>> get(@PathVariable int id) {
        logger.debug("MyBatis - Received get request for user ID: {}", id);
        User user = myBatisUserService.get(id);
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
    curl -X GET "http://localhost:8080/chatgpt/api/mybatis/users/all" -H "Accept: application/json"
    */
    @GetMapping("/all")
    public ResponseEntity<Response<List<User>>> getAll() {
        logger.debug("MyBatis - Received get all users request");
        List<User> users = myBatisUserService.getAll();
        logger.debug("MyBatis - users: {}", users);
        Response<List<User>> response = Response.success("Users retrieved successfully", users, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method=RequestMethod.GET, value = "/old/all")
    public ResponseEntity<Response<List<User>>> oldGetAll() {
        return getAll();
    }

    //------------------------------

    /*
    curl -X GET "http://localhost:8080/chatgpt/api/mybatis/users/test" -H "Accept: application/json"
    */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.debug("MyBatis - calling test");
        return ResponseEntity.ok("MyBatis API is working!");
    }
}