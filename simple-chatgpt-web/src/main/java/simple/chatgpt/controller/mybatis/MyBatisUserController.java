package simple.chatgpt.controller.mybatis;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simple.chatgpt.pojo.mybatis.MyBatisUserUser;
import simple.chatgpt.service.mybatis.MyBatisUserService;
import simple.chatgpt.util.Response;

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

    private final MyBatisUserService myBatisUserService;

    @Autowired
    public MyBatisUserController(MyBatisUserService myBatisUserService) {
        logger.info("MyBatisUserController initialized!");
        this.myBatisUserService = myBatisUserService;
    }

    //------------------------------

    /*
    curl -X POST http://localhost:8080/chatgpt/api/mybatis/users/add -H "Content-Type: application/json" -H "Accept: application/json" -d '{"name": "Test3", "email": "test@example3.com"}'
    */
    @PostMapping(value = "/add",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<MyBatisUserUser>> save(@RequestBody MyBatisUserUser user) {
    	logger.debug("##########################");
    	logger.debug("save user: {}", user);
    	logger.debug("##########################");

        boolean isUpdate = user.getId() > 0;
        MyBatisUserUser savedUser = myBatisUserService.save(user);

        if (isUpdate) {
            logger.debug("MyBatis - Updated user: {}", savedUser);
            Response<MyBatisUserUser> response = Response.success("User updated successfully", savedUser, HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } else {
            logger.debug("MyBatis - Created new user: {}", savedUser);
            Response<MyBatisUserUser> response = Response.success("User created successfully", savedUser, HttpStatus.CREATED.value());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    @PostMapping(value = "/old/add",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<MyBatisUserUser>> addUser(@RequestBody MyBatisUserUser user) {
        return save(user);
    }

    //------------------------------

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<MyBatisUserUser>> update(@PathVariable int id, @RequestBody MyBatisUserUser user) {
    	logger.debug("##########################");
        logger.debug("update user ID: {} with user: {}", id, user);
        logger.debug("##########################");
        
        // Ensure the user ID matches the path variable
        user.setId(id);

        // Check if user exists first
        MyBatisUserUser existingUser = myBatisUserService.get(id);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Response.error("User not found", null, HttpStatus.NOT_FOUND.value())
            );
        }

        MyBatisUserUser updatedUser = myBatisUserService.save(user);
        logger.debug("MyBatis - Updated user: {}", updatedUser);

        Response<MyBatisUserUser> response = Response.success("User updated successfully", updatedUser, HttpStatus.OK.value());
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
    public ResponseEntity<Response<MyBatisUserUser>> get(@PathVariable int id) {
        logger.debug("MyBatis - Received get request for user ID: {}", id);
        MyBatisUserUser user = myBatisUserService.get(id);
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
    public ResponseEntity<Response<List<MyBatisUserUser>>> getAll() {
        logger.debug("MyBatis - Received get all users request");
        List<MyBatisUserUser> users = myBatisUserService.getAll();
        logger.debug("MyBatis - users: {}", users);
        Response<List<MyBatisUserUser>> response = Response.success("Users retrieved successfully", users, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method=RequestMethod.GET, value = "/old/all")
    public ResponseEntity<Response<List<MyBatisUserUser>>> oldGetAll() {
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

    //------------------------------

    /**
     * Get users with paging, sorting, and filtering
     * Example: GET /mybatis/users/paged?page=1&size=10&sortField=name&sortOrder=ASC&firstName=John&city=NY
     */
    @GetMapping(value = "/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<Object>> getUsersPaged(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String addressLine1,
            @RequestParam(required = false) String addressLine2,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String country
    ) {
        logger.debug("MyBatis - Received paged user request: page={}, size={}, sortField={}, sortOrder={}, filters...", page, size, sortField, sortOrder);
        List<MyBatisUserUser> users = myBatisUserService.getUsersPagedFiltered(
            page, size, sortField, sortOrder,
            firstName, lastName, email, addressLine1, addressLine2, city, state, country
        );
        int total = myBatisUserService.getTotalUserCountFiltered(
            firstName, lastName, email, addressLine1, addressLine2, city, state, country
        );
        // Return both users and total count for frontend paging
        return ResponseEntity.ok(
                Response.success("Paged users fetched", new java.util.HashMap<String, Object>() {{
                    put("users", users);
                    put("total", total);
                }}, HttpStatus.OK.value())
        );
    }

    @GetMapping("/paged")
    public ResponseEntity<Response<Object>> getPaged(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "ASC") String sortOrder) {
        logger.debug("MyBatis - Received paged users request: page={}, size={}, sortField={}, sortOrder={}", page, size, sortField, sortOrder);
        List<MyBatisUserUser> users = myBatisUserService.getUsersPaged(page, size, sortField, sortOrder);
        int total = myBatisUserService.getTotalUserCount();
        var data = new java.util.HashMap<String, Object>();
        data.put("users", users);
        data.put("total", total);
        Response<Object> response = Response.success("Paged users retrieved successfully", data, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}